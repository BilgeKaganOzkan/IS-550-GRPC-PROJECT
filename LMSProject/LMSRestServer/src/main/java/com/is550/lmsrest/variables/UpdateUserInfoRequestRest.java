package com.is550.lmsrest.variables;

public class UpdateUserInfoRequestRest {

    private int userID;
    private UserRest user;


    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public UserRest getUser() {
        return user;
    }

    public void setUser(UserRest user) {
        this.user = user;
    }
}
