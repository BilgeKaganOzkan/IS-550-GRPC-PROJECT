package com.is550.lmsrest.variables;

public enum BookTypeRest {

    BIOLOGY("biology"),
    PHYSICS("physics"),
    MATHEMATICS("mathematics"),
    SOCIAL("social");
    private final String value;

    BookTypeRest(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BookTypeRest fromValue(String v) {
        for (BookTypeRest c: BookTypeRest.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
