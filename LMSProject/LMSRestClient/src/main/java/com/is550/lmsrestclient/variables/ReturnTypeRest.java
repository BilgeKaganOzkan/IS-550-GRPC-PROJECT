package com.is550.lmsrestclient.variables;

public enum ReturnTypeRest {

    OK("ok");
    private final String value;

    ReturnTypeRest(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ReturnTypeRest fromValue(String v) {
        for (ReturnTypeRest c: ReturnTypeRest.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}

