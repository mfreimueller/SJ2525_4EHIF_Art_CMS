package com.mfreimueller.art.foundation;

public class DataConstraintException extends RuntimeException {
    private DataConstraintException(String message) {
        super(message);
    }

    private DataConstraintException(String message, Throwable cause) {}

    public static DataConstraintException forUnmappedEnumValue(Character c, Class<? extends Enum<?>> enumClass) {
        return new DataConstraintException("Unmapped enum value '%c' for enum %s".formatted(c, enumClass));
    }

    public static DataConstraintException forDuplicatedEntry(Class<?> clazz, Long id) {
        return new DataConstraintException("Duplicated entry of type %s for id %d".formatted(clazz, id));
    }

    public static DataConstraintException forMissingEntry(Class<?> clazz, Long id) {
        return new DataConstraintException("No entry of type %s found for id %d".formatted(clazz, id));
    }

    public static DataConstraintException forCircularReference(Class<?> clazz, Long id) {
        return new DataConstraintException("Attempted to create circular reference of class %s for id %d".formatted(clazz, id));
    }
}
