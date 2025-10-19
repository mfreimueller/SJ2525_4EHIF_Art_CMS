package com.mfreimueller.art.persistence.converters;

import com.mfreimueller.art.domain.Source;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LinkTypeConverterTest {

    private final LinkTypeConverter converter = new LinkTypeConverter();

    @ParameterizedTest
    @MethodSource("mappingData")
    void can_convert_to_db_value_and_back(Source.LinkType linkType, Character dbValue) {
        var result = converter.convertToDatabaseValue(linkType);
        assertThat(result).isEqualTo(dbValue);
        assertThat(converter.convertToEntity(result)).isEqualTo(linkType);
    }

    @Test
    void can_convert_null_values_safely() {
        assertThat(converter.convertToEntity(null)).isNull();
        assertThat(converter.convertToDatabaseValue(null)).isNull();
    }

    private static Stream<Arguments> mappingData() {
        return Stream.of(
                Arguments.of(Source.LinkType.Url, 'u'),
                Arguments.of(Source.LinkType.Absolute, 'a'),
                Arguments.of(Source.LinkType.Relative, 'r')
        );
    }
}