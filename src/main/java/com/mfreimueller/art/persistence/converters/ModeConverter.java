package com.mfreimueller.art.persistence.converters;

import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.SlideshowContent;
import com.mfreimueller.art.exceptions.DataConstraintException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ModeConverter implements AttributeConverter<SlideshowContent.Mode, Character> {
    @Override
    public Character convertToDatabaseColumn(SlideshowContent.Mode mode) {
        return switch (mode) {
            case null -> null;
            case SlideshowContent.Mode.Auto -> 'a';
            case SlideshowContent.Mode.Hybrid -> 'h';
            case SlideshowContent.Mode.Manual -> 'm';
        };
    }

    @Override
    public SlideshowContent.Mode convertToEntityAttribute(Character character) {
        return switch (character) {
            case null -> null;
            case 'a' -> SlideshowContent.Mode.Auto;
            case 'h' -> SlideshowContent.Mode.Hybrid;
            case 'm' -> SlideshowContent.Mode.Manual;
            default -> throw DataConstraintException.forUnmappedEnumValue(character, Creator.Role.class);
        };
    }
}
