package com.is550.lmsrest.variables;

public enum UserTypeRest {

    LIBRARIAN("librarian"),
    USER("user"),
    ADMIN("admin");
    private final String value;

    UserTypeRest(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UserTypeRest fromValue(String v) {
        for (UserTypeRest c: UserTypeRest.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
