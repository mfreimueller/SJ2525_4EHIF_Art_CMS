package com.mfreimueller.art.persistence.converters;

import com.mfreimueller.art.domain.Source;
import com.mfreimueller.art.exceptions.DataConstraintException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LinkTypeConverter implements AttributeConverter<Source.LinkType, Character> {
    @Override
    public Character convertToDatabaseColumn(Source.LinkType linkType) {
        return switch (linkType) {
            case null -> null;
            case Source.LinkType.Url -> 'u';
            case Source.LinkType.Absolute -> 'a';
            case Source.LinkType.Relative -> 'r';
        };
    }

    @Override
    public Source.LinkType convertToEntityAttribute(Character character) {
        return switch (character) {
            case null -> null;
            case 'u' -> Source.LinkType.Url;
            case 'a' -> Source.LinkType.Absolute;
            case 'r' -> Source.LinkType.Relative;
            default -> throw DataConstraintException.forUnmappedEnumValue(character, Source.LinkType.class);
        };
    }
}
