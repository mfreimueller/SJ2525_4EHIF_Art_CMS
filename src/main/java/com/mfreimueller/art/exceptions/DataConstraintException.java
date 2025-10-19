package com.mfreimueller.art.exceptions;

public class DataConstraintException extends RuntimeException {
    private DataConstraintException(String message) {
        super(message);
    }

    private DataConstraintException(String message, Throwable cause) {}

    public static DataConstraintException forUnmappedEnumValue(Character c, Class<? extends Enum<?>> enumClass) {
        return new DataConstraintException("Unmapped enum value '%c' for enum %s".formatted(c, enumClass));
    }
}
