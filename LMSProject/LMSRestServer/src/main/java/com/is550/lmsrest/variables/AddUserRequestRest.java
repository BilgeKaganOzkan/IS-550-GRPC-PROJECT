package com.is550.lmsrest.variables;

public class AddUserRequestRest {

    private UserRest user;
    private Long loginID;

    public UserRest getUser() {
        return user;
    }

    public void setUser(UserRest user) {
        this.user = user;
    }

    public Long getLoginID() {
        return loginID;
    }

    public void setLoginID(Long loginID) {
        this.loginID = loginID;
    }
}

