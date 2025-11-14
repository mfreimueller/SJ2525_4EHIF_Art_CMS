package com.mfreimueller.art.persistence.converters;

import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.foundation.DataConstraintException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Creator.Role, Character> {
    @Override
    public Character convertToDatabaseColumn(Creator.Role role) {
        return switch (role) {
            case null -> null;
            case Creator.Role.Admin -> 'a';
            case Creator.Role.Editor -> 'e';
            case Creator.Role.Viewer -> 'v';
        };
    }

    @Override
    public Creator.Role convertToEntityAttribute(Character character) {
        return switch (character) {
            case null -> null;
            case 'a' -> Creator.Role.Admin;
            case 'e' -> Creator.Role.Editor;
            case 'v' -> Creator.Role.Viewer;
            default -> throw DataConstraintException.forUnmappedEnumValue(character, Creator.Role.class);
        };
    }
}
