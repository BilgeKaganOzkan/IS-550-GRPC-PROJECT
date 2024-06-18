package com.lms.grpcServer;

import com.lms.grpc.*;
import org.jdbi.v3.core.Jdbi;

import java.sql.*;

public class LMSDatabase {
    private final Jdbi jdbi;

    public LMSDatabase(String databaseUrl) {
        this.jdbi = Jdbi.create(databaseUrl);
        initializeDatabase();
    }

    private void initializeDatabase() {
        jdbi.useHandle(handle -> handle.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, studentID TEXT, name TEXT, surname TEXT, email TEXT UNIQUE, password TEXT, tel_number TEXT, location TEXT, department TEXT, type INTEGER)"));
    }

    public boolean userExistsEmail(String email) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT COUNT(*) FROM users WHERE email = :email")
                        .bind("email", email)
                        .mapTo(Integer.class)
                        .one()
        ) > 0;
    }

    public boolean userExistsID(int id) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT COUNT(*) FROM users WHERE id = :id")
                        .bind("id", id)
                        .mapTo(Integer.class)
                        .one()
        ) > 0;
    }

    public boolean isAdmin(int loginID) throws Exception {
        if(!userExistsID(loginID)){
            throw new Exception("No user found");
        }
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT type FROM users WHERE id = :id")
                        .bind("id", loginID)
                        .mapTo(Integer.class)
                        .one()
        ) == 2;
    }

    public boolean isLibrarian(int loginID) throws Exception {
        if(!userExistsID(loginID)){
            throw new Exception("No user found");
        }
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT type FROM users WHERE id = :id")
                        .bind("id", loginID)
                        .mapTo(Integer.class)
                        .one()
        ) == 1;
    }

    public GetLoginIDResponse getLoginID(String email, String password) throws Exception {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT id, type FROM users WHERE email = :email AND password = :password")
                        .bind("email", email)
                        .bind("password", password)
                        .map((rs, ctx) -> {
                            long id = rs.getInt("id");
                            int type = rs.getInt("type");
                            return GetLoginIDResponse.newBuilder()
                                    .setLoginID((int) id)
                                    .setType(intToUserType(type))
                                    .build();
                        })
                        .findOne()
                        .orElseThrow(() -> new Exception("Invalid email or password"))
        );
    }

    public AddUserResponse addUser(int loginID, int studentID, String name, String surname, String email, String password, String telNumber, String location, String department, UserType type) throws Exception {
        if (!isAdmin(loginID)) {
            throw new Exception("Permission denied: User is not admin");
        }

        if(userExistsEmail(email)){
            throw new Exception("User already exists");
        }

        int typeInt = userTypetoInt(type);

        return jdbi.withHandle(handle -> {
            int userID = handle.createUpdate("INSERT INTO users (studentID, name, surname, email, password, tel_number, location, department, type) VALUES (:studentID, :name, :surname, :email, :password, :tel_number, :location, :department, :type)")
                    .bind("studentID", studentID)
                    .bind("name", name)
                    .bind("surname", surname)
                    .bind("email", email)
                    .bind("password", password)
                    .bind("tel_number", telNumber)
                    .bind("location", location)
                    .bind("department", department)
                    .bind("type", typeInt)
                    .executeAndReturnGeneratedKeys("id")
                    .mapTo(int.class)
                    .one();

            return AddUserResponse.newBuilder()
                    .setUserID(userID)
                    .build();
        });
    }

    public void deleteUser(int loginID, int userID) throws Exception {
        if (!isAdmin(loginID)) {
            throw new Exception("Permission denied: User is not admin");
        }

        if(!userExistsID(userID)){
            throw new Exception("User not found");
        }

        jdbi.useHandle(handle ->
                handle.createUpdate("DELETE FROM users WHERE id = :id")
                        .bind("id", userID)
                        .execute()
        );
    }

    public void changePassword(int loginID, String oldPassword, String newPassword) throws Exception {
        if(!jdbi.withHandle(handle ->
                handle.createQuery("SELECT id FROM users WHERE id = :id AND password = :password")
                        .bind("id", loginID)
                        .bind("password", oldPassword)
                        .mapTo(String.class)
                        .findOne()
        ).orElse("").equals(Integer.toString(loginID))){
            throw new Exception("Wrong email or password");
        }

        jdbi.useHandle(handle ->
                handle.createUpdate("UPDATE users SET password = :password WHERE id = :id")
                        .bind("id", loginID)
                        .bind("password", newPassword)
                        .execute()
        );
    }

    public void updateContactInfo(int loginID, String newTelNumber, String newLocation) throws Exception {
        if(jdbi.withHandle(handle ->
                handle.createQuery("SELECT email FROM users WHERE id = :id")
                        .bind("id", loginID)
                        .mapTo(String.class)
                        .findOne()
        ).isEmpty()){
            throw new Exception("Wrong email or password");
        }

        jdbi.useHandle(handle ->
                handle.createUpdate("UPDATE users SET tel_number = :tel_number, location = :location WHERE id = :id")
                        .bind("id", loginID)
                        .bind("tel_number", newTelNumber)
                        .bind("location", newLocation)
                        .execute()
        );
    }

    public void updateUserInfo(int loginID, int userID, int studentID, String name, String surname, String email, String password, String telNumber, String location, String department, UserType type) throws Exception {
        if (!isAdmin(loginID)) {
            throw new Exception("Permission denied: User is not admin");
        }

        if(!userExistsID(userID)){
            throw new Exception("User not found");
        }

        int typeInt = userTypetoInt(type);

        jdbi.useHandle(handle ->
                handle.createUpdate("UPDATE users SET studentID = :studentID, name = :name, surname = :surname, password = :password, tel_number = :tel_number, location = :location, department = :department, type = :type WHERE id = :id")
                        .bind("id", userID)
                        .bind("studentID", studentID)
                        .bind("name", name)
                        .bind("surname", surname)
                        .bind("email", email)
                        .bind("password", password)
                        .bind("tel_number", telNumber)
                        .bind("location", location)
                        .bind("department", department)
                        .bind("type", typeInt)
                        .execute()
        );
    }

    public User getUserInfo(int userID) throws Exception {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT studentID, name, surname, email, password, tel_number, location, department, type FROM users WHERE id = :id")
                        .bind("id", userID)
                        .map((rs, ctx) -> User.newBuilder()
                                .setStudentID(rs.getInt("studentID"))
                                .setName(rs.getString("name"))
                                .setSurname(rs.getString("surname"))
                                .setEmail(rs.getString("email"))
                                .setPassword(rs.getString("password"))
                                .setTelNumber(rs.getString("tel_number"))
                                .setLocation(rs.getString("location"))
                                .setDepartment(rs.getString("department"))
                                .setType(intToUserType(rs.getInt("type")))
                                .build())
                        .findOne()
                        .orElseThrow(() -> new Exception("User not found"))
        );
    }

    public long translateStudentIdToUserId(long studentId) throws Exception {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT ID FROM USERS WHERE StudentId = :studentId")
                        .bind("studentId", studentId)
                        .mapTo(Long.class)
                        .findOne()
                        .orElseThrow(() -> new Exception("Student ID Not Found"))
        );
    }

    public long translateUserIdToStudentId(long userId) throws Exception {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT StudentID FROM USERS WHERE ID = :userId")
                        .bind("userId", userId)
                        .mapTo(Long.class)
                        .findOne()
                        .orElseThrow(() -> new Exception("User ID Not Found"))
        );
    }

    public static int userTypetoInt(UserType userType) {
        switch (userType) {
            case USER:
                return 0;
            case LIBRARIAN:
                return 1;
            case ADMIN:
                return 2;
            default:
                throw new IllegalArgumentException("Unknown user type: " + userType);
        }
    }

    public static UserType intToUserType(int value) {
        switch (value) {
            case 0:
                return UserType.USER;
            case 1:
                return UserType.LIBRARIAN;
            case 2:
                return UserType.ADMIN;
            default:
                throw new IllegalArgumentException("Unknown user type value: " + value);
        }
    }
}