package com.mfreimueller.art.persistence.converters;

import com.mfreimueller.art.domain.SlideshowContent;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ModeConverterTest {

    private final ModeConverter converter = new ModeConverter();

    @ParameterizedTest
    @MethodSource("mappingData")
    void can_convert_to_db_value_and_back(SlideshowContent.Mode mode, Character dbValue) {
        var result = converter.convertToDatabaseColumn(mode);
        assertThat(result).isEqualTo(dbValue);
        assertThat(converter.convertToEntityAttribute(result)).isEqualTo(mode);
    }

    @Test
    void can_convert_null_values_safely() {
        assertThat(converter.convertToEntityAttribute(null)).isNull();
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    private static Stream<Arguments> mappingData() {
        return Stream.of(
                Arguments.of(SlideshowContent.Mode.AUTO, 'a'),
                Arguments.of(SlideshowContent.Mode.HYBRID, 'h'),
                Arguments.of(SlideshowContent.Mode.MANUAL, 'm')
        );
    }
}