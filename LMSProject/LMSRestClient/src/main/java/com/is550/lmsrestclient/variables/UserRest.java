package com.is550.lmsrestclient.variables;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserRest {
    @JsonProperty("studentID")
    protected String studentID;
    @JsonProperty("name")
    protected String name;
    @JsonProperty("surname")
    protected String surname;
    @JsonProperty("email")
    protected String email;
    @JsonProperty("password")
    protected String password;
    @JsonProperty("telNumber")
    protected String telNumber;
    @JsonProperty("location")
    protected String location;
    @JsonProperty("department")
    protected String department;
    @JsonProperty("type")
    protected UserTypeRest type;

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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