package com.is550.lmsrestclient.variables;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddUserRequestRest {

    @JsonProperty("user")
    private UserRest user;

    @JsonProperty("loginID")
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

