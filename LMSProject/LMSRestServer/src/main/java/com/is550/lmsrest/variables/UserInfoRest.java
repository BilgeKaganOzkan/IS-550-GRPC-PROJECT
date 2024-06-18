package com.is550.lmsrest.variables;

public class UserInfoRest {
    protected int studentID;
    protected String name;
    protected String surname;
    protected String telNumber;
    protected String location;
    protected String department;
    protected UserTypeRest type;

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public UserTypeRest getType() {
        return type;
    }

    public void setType(UserTypeRest type) {
        this.type = type;
    }
}