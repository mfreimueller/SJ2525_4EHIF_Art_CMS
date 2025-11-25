package com.mfreimueller.art.persistence.converters;

import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.SlideshowContent;
import com.mfreimueller.art.foundation.DataConstraintException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ModeConverter implements AttributeConverter<SlideshowContent.Mode, Character> {
    @Override
    public Character convertToDatabaseColumn(SlideshowContent.Mode mode) {
        return switch (mode) {
            case null -> null;
            case SlideshowContent.Mode.AUTO -> 'a';
            case SlideshowContent.Mode.HYBRID -> 'h';
            case SlideshowContent.Mode.MANUAL -> 'm';
        };
    }

    @Override
    public SlideshowContent.Mode convertToEntityAttribute(Character character) {
        return switch (character) {
            case null -> null;
            case 'a' -> SlideshowContent.Mode.AUTO;
            case 'h' -> SlideshowContent.Mode.HYBRID;
            case 'm' -> SlideshowContent.Mode.MANUAL;
            default -> throw DataConstraintException.forUnmappedEnumValue(character, Creator.Role.class);
        };
    }
}
