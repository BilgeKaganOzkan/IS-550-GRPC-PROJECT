package com.is550.lmsrest.database;

import com.is550.lmsrest.exceptions.AuthenticationFailureException;
import com.is550.lmsrest.exceptions.DatabaseOperationException;
import com.is550.lmsrest.grpcclient.GrpcClient;
import com.is550.lmsrest.variables.*;
import com.lms.grpc.GetLoginIDResponse;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.GregorianCalendar;

@Component
public class LMSDatabase {

    public final GrpcClient grpcClient;
    String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "\\src\\main\\resources\\lms.db";

    public LMSDatabase() {
        grpcClient = new GrpcClient("localhost", 8080); // gRPC sunucu adresini ve portunu buraya ekleyin
    }

    public UserLoginInfosRest findUserId(String email, String password) {
        GetLoginIDResponse response =  grpcClient.getLoginID(email, password);

        UserLoginInfosRest userLoginInfos = new UserLoginInfosRest();
        userLoginInfos.setUserId(response.getLoginID());
        if(response.getType() == com.lms.grpc.UserType.USER)
        {
            userLoginInfos.setUserType(UserTypeRest.USER);
        }else if(response.getType() == com.lms.grpc.UserType.ADMIN) {
            userLoginInfos.setUserType(UserTypeRest.ADMIN);
        }else {
            userLoginInfos.setUserType(UserTypeRest.LIBRARIAN);
        }
        return userLoginInfos;
    }

    public List<BookRest> findBooksByName(long loginId, String name) {
        long returnVal = -1;

        if (checkLoginIdAuthentication(loginId)) {
            String query = "SELECT * FROM BOOKS WHERE Name = ?";
            List<BookRest> books = new ArrayList<>();

            try (Connection connection = DriverManager.getConnection(url)) {
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, name);

                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    returnVal = 0;

                    long id = (long) resultSet.getInt("Id");
                    String author = resultSet.getString("Author");
                    String bookType = resultSet.getString("Type");
                    String location = resultSet.getString("Location");
                    String availability = resultSet.getString("Available");

                    BookRest book = new BookRest();

                    book.setId(id);
                    book.setName(name);
                    book.setAuthor(author);
                    book.setLocation(location);
                    book.setAvailable(availability);
                    book.setType(translateStringToBookType(bookType));

                    books.add(book);
                }
            } catch (SQLException e) {
                throw new DatabaseOperationException("Server SQL Error");
            }
            if (returnVal != -1) {
                return books;
            } else {
                throw new DatabaseOperationException("No books found");
            }
        } else {
            throw new AuthenticationFailureException("Your identity has not been verified");
        }
    }

    public List<BookRest> findBooksByAuthor(long loginId, String author) {
        long returnVal = -1;

        if (checkLoginIdAuthentication(loginId)) {
            String query = "SELECT * FROM BOOKS WHERE Author = ?";
            List<BookRest> books = new ArrayList<>();

            try (Connection connection = DriverManager.getConnection(url)) {
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, author);

                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    returnVal = 0;

                    long id = (long) resultSet.getInt("Id");
                    String name = resultSet.getString("Name");
                    String bookType = resultSet.getString("Type");
                    String location = resultSet.getString("Location");
                    String availability = resultSet.getString("Available");

                    BookRest book = new BookRest();

                    book.setId(id);
                    book.setName(name);
                    book.setAuthor(author);
                    book.setLocation(location);
                    book.setAvailable(availability);
                    book.setType(translateStringToBookType(bookType));

                    books.add(book);
                }
            } catch (SQLException e) {
                throw new DatabaseOperationException("Server SQL Error");
            }

            if (returnVal != -1) {
                return books;
            } else {
                throw new DatabaseOperationException("No books found");
            }
        } else {
            throw new AuthenticationFailureException("Your identity has not been verified");
        }
    }

    public BookRest findBookById(long loginId, long bookId) {
        if (checkLoginIdAuthentication(loginId)) {
            String query = "SELECT * FROM BOOKS WHERE id = ?";
            BookRest book = new BookRest();

            try (Connection connection = DriverManager.getConnection(url)) {
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setLong(1, bookId);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String name = resultSet.getString("Name");
                    String author = resultSet.getString("Author");
                    String bookType = resultSet.getString("Type");
                    String location = resultSet.getString("Location");
                    String availability = resultSet.getString("Available");

                    book.setId(bookId);
                    book.setName(name);
                    book.setAuthor(author);
                    book.setLocation(location);
                    book.setAvailable(availability);
                    book.setType(translateStringToBookType(bookType));

                    return book;
                }
                throw new DatabaseOperationException("No books found");
            } catch (SQLException e) {
                throw new DatabaseOperationException("Server SQL Error");
            }
        } else {
            throw new AuthenticationFailureException("Your identity has not been verified");
        }
    }

    public List<UserBorrowingInfosRest> findUserInfo(Long loginId, Long studentId) {
        long returnVal = -1;

        if (checkLoginIdAuthentication(loginId)) {
            long userId = translateStudentIdToUserId(studentId);
            boolean loginUserIsEqual = checkLoginIdIsLibrarian(loginId);
            if ((!loginId.equals(userId) && loginUserIsEqual) || loginId.equals(userId)) {
                List<UserBorrowingInfosRest> userInfos = new ArrayList<>();

                String query = "SELECT * FROM BORROWING WHERE UserId = ?";

                try (Connection connection = DriverManager.getConnection(url)) {
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setLong(1, userId);

                    ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        returnVal = 0;

                        long bookId = (long) resultSet.getInt("BookId");
                        long borrowTime = (long) resultSet.getInt("BorrowTime");
                        long dueDate = (long) resultSet.getInt("DueDate");
                        long returningTime = (long) resultSet.getInt("ReturningTime");
                        long fine = (long) resultSet.getInt("Fine");
                        String paid = resultSet.getString("Paid");
                        BookRest book = findBookById(loginId, bookId);

                        UserBorrowingInfosRest userInfo = new UserBorrowingInfosRest();

                        userInfo.setBook(book);
                        userInfo.setFine(fine);
                        userInfo.setPaid(paid);

                        try {
                            Date date = new Date(borrowTime * 1000L);
                            GregorianCalendar gregorianCalendar = new GregorianCalendar();
                            gregorianCalendar.setTime(date);
                            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
                            XMLGregorianCalendar xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
                            userInfo.setBorrowingTime(xmlGregorianCalendar);

                            date.setTime(dueDate * 1000L);
                            gregorianCalendar.setTime(date);
                            xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
                            userInfo.setDueDate(xmlGregorianCalendar);

                            date.setTime(returningTime * 1000L);
                            gregorianCalendar.setTime(date);
                            xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
                            userInfo.setReturningTime(xmlGregorianCalendar);
                        } catch (Exception e) {
                            throw new DatabaseOperationException("Server Error");
                        }
                        userInfos.add(userInfo);
                    }
                } catch (SQLException e) {
                    throw new DatabaseOperationException("Server SQL Error");
                }
                if (returnVal != -1) {
                    return userInfos;
                } else {
                    throw new DatabaseOperationException("No record found");
                }
            } else {
                throw new AuthenticationFailureException("You are not authorized");
            }
        } else {
            throw new AuthenticationFailureException("Your identity has not been verified");
        }
    }

    public ReturnTypeRest addBorrowBook(Long loginId, Long studentId, Long bookId,
                                        XMLGregorianCalendar borrowingTime,
                                        XMLGregorianCalendar dueDate) {

        if (checkLoginIdIsLibrarian(loginId)) {
            long userId = translateStudentIdToUserId(studentId);
            try (Connection connection = DriverManager.getConnection(url)) {
                String query = "INSERT INTO BORROWING (UserId, BookId, BorrowTime, DueDate) VALUES (?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setLong(1, userId);
                statement.setLong(2, bookId);

                Date borrowingTimeXml = borrowingTime.toGregorianCalendar().getTime();
                long time = borrowingTimeXml.getTime() / 1000L;
                statement.setLong(3, time);

                Date dueDateXml = dueDate.toGregorianCalendar().getTime();
                time = dueDateXml.getTime() / 1000L;
                statement.setLong(4, time);

                statement.executeUpdate();

                setAvailability("no", bookId, connection);
                return ReturnTypeRest.OK;
            } catch (SQLException e) {
                throw new DatabaseOperationException("Server SQL Error");
            }
        } else {
            throw new AuthenticationFailureException("You are not authorized");
        }
    }

    public ReturnTypeRest addReturnBook(Long loginId, Long studentId, Long bookId,
                                        XMLGregorianCalendar returningTime) {

        long fine = 0;
        if (checkLoginIdIsLibrarian(loginId)) {
            long userId = translateStudentIdToUserId(studentId);
            try (Connection connection = DriverManager.getConnection(url)) {
                String query = "SELECT * FROM BORROWING WHERE UserId = ? AND BookId = ? AND ReturningTime IS NULL";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setLong(1, userId);
                statement.setLong(2, bookId);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    long dueDate = (long) resultSet.getInt("DueDate");

                    Date returningTimeXml = returningTime.toGregorianCalendar().getTime();
                    long time = returningTimeXml.getTime() / 1000L;

                    if (time > dueDate) {
                        long weeksLate = (time - dueDate) / (7 * 24 * 60 * 60);
                        fine = (Long) (weeksLate * 10) + 10;
                    }

                    query = "UPDATE BORROWING SET ReturningTime = ?, Fine = ?, Paid = ? WHERE UserId = ? AND BookId = ? AND ReturningTime IS NULL";
                    statement = connection.prepareStatement(query);

                    statement.setLong(1, time);
                    statement.setLong(2, fine);
                    statement.setString(3, "No");
                    statement.setLong(4, userId);
                    statement.setLong(5, bookId);

                    statement.executeUpdate();

                    setAvailability("yes", bookId, connection);
                    return ReturnTypeRest.OK;
                }
                throw new DatabaseOperationException("No record found");
            } catch (SQLException e) {
                throw new DatabaseOperationException("Server SQL Error");
            }
        } else {
            throw new AuthenticationFailureException("You are not authorized");
        }
    }

    public long addBook(Long loginId, String name, BookTypeRest bookType, String author, String location) {
        if (checkLoginIdIsLibrarian(loginId)) {
            try (Connection connection = DriverManager.getConnection(url)) {
                String query = "SELECT MAX(ID) AS MAX_ID FROM BOOKS";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    long maxId = resultSet.getLong("MAX_ID");
                    query = "INSERT INTO BOOKS (ID, Name, Author, Type, Location, Available) VALUES (?, ?, ?, ?, ?, ?)";
                    statement = connection.prepareStatement(query);
                    statement.setLong(1, maxId + 1);
                    statement.setString(2, name);
                    statement.setString(3, author);
                    statement.setString(5, location);
                    statement.setString(6, "yes");
                    statement.setString(4, translateBookTypeToString(bookType));

                    int rowCount = statement.executeUpdate();

                    if (rowCount > 0) {
                        query = "SELECT MAX(ID) AS MAX_ID FROM BOOKS";
                        statement = connection.prepareStatement(query);
                        resultSet = statement.executeQuery();
                        if (resultSet.next()) {
                            maxId = resultSet.getLong("MAX_ID");
                            return maxId;
                        }
                    } else {
                        throw new DatabaseOperationException("Server SQL Error");
                    }
                }
                throw new DatabaseOperationException("Server SQL Error");
            } catch (SQLException e) {
                throw new DatabaseOperationException("Server SQL Error");
            }
        } else {
            throw new AuthenticationFailureException("You are not authorized");
        }
    }

    public ReturnTypeRest deleteBook(Long loginId, Long bookId) {
        if (checkLoginIdIsLibrarian(loginId)) {
            try (Connection connection = DriverManager.getConnection(url)) {
                String query = "DELETE FROM BOOKS WHERE ID = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setLong(1, bookId);

                statement.executeUpdate();

                return ReturnTypeRest.OK;
            } catch (SQLException e) {
                throw new DatabaseOperationException("Server SQL Error");
            }
        } else {
            throw new AuthenticationFailureException("You are not authorized");
        }
    }

    public boolean checkLoginIdAuthentication(Long loginId) {
        return grpcClient.isUserExist(loginId);
    }

    public boolean checkLoginIdIsLibrarian(Long loginId) {
        return grpcClient.isLibrarian(loginId);
    }

    public boolean checkLoginIdIsAdmin(Long loginId) {
        return grpcClient.isAdmin(loginId);
    }

    public void setAvailability(String availability, Long bookId, Connection connection) {
        try (connection) {
            String query = "UPDATE BOOKS SET Available = ? WHERE ID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, availability);
            statement.setLong(2, bookId);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseOperationException("Server SQL Error");
        }
    }

    public UserTypeRest getRealUserType(long type) {
        if (type == 0) {
            return UserTypeRest.USER;
        } else {
            return UserTypeRest.LIBRARIAN;
        }
    }

    public long translateStudentIdToUserId(long studentId) {
        return grpcClient.translateStudentIdToUserId(Math.toIntExact(studentId)).getUserID();
    }

    public long translateUserIdToStudentId(long userId) {
        return grpcClient.translateUserIdToStudentId(Math.toIntExact(userId)).getStudentID();
    }

    public String translateBookTypeToString(BookTypeRest bookType) {
        if (bookType.equals(BookTypeRest.BIOLOGY)) {
            return "Biology";
        } else if (bookType.equals(BookTypeRest.MATHEMATICS)) {
            return "Mathematics";
        } else if (bookType.equals(BookTypeRest.PHYSICS)) {
            return "Physics";
        } else if (bookType.equals(BookTypeRest.SOCIAL)) {
            return "Social";
        } else {
            throw new DatabaseOperationException("Undefined Type");
        }
    }

    public BookTypeRest translateStringToBookType(String bookType) {
        if (bookType.equals("Biology")) {
            return BookTypeRest.BIOLOGY;
        } else if (bookType.equals("Mathematics")) {
            return BookTypeRest.MATHEMATICS;
        } else if (bookType.equals("Physics")) {
            return BookTypeRest.PHYSICS;
        } else if (bookType.equals("Social")) {
            return BookTypeRest.SOCIAL;
        } else {
            throw new DatabaseOperationException("Undefined Type");
        }
    }
}
