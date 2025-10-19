package com.mfreimueller.art.persistence.converters;

import com.mfreimueller.art.domain.Creator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RoleConverterTest {

    private final RoleConverter converter = new RoleConverter();

    @ParameterizedTest
    @MethodSource("mappingData")
    void can_convert_to_db_value_and_back(Creator.Role role, Character dbValue) {
        var result = converter.convertToDatabaseValue(role);
        assertThat(result).isEqualTo(dbValue);
        assertThat(converter.convertToEntity(result)).isEqualTo(role);
    }

    @Test
    void can_convert_null_values_safely() {
        assertThat(converter.convertToEntity(null)).isNull();
        assertThat(converter.convertToDatabaseValue(null)).isNull();
    }

    private static Stream<Arguments> mappingData() {
        return Stream.of(
                Arguments.of(Creator.Role.Admin, 'a'),
                Arguments.of(Creator.Role.Editor, 'e'),
                Arguments.of(Creator.Role.Viewer, 'v')
        );
    }
}