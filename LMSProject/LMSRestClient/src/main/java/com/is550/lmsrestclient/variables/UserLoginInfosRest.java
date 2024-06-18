package com.is550.lmsrestclient.variables;


public class UserLoginInfosRest {
    protected long userId;
    protected UserTypeRest userType;

    public long getUserId() {
        return userId;
    }
    public void setUserId(long value) {
        this.userId = value;
    }
    public UserTypeRest getUserType() {
        return userType;
    }
    public void setUserType(UserTypeRest value) {
        this.userType = value;
    }
}