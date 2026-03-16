package com.mfreimueller.art.mapper;

import com.mfreimueller.art.domain.AudioContent;
import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.dto.ContentDto;
import com.mfreimueller.art.dto.PointOfInterestDto;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.mappers.ContentMapper;
import com.mfreimueller.art.mappers.PointOfInterestMapper;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ContentMapperTest {

    @Autowired
    private DateTimeFactory dateTimeFactory;

    @Autowired
    private ContentMapper mapper;

    private AudioContent content;

    @BeforeEach
    void setUp() {
        content = AudioContent.builder()
                .id(new Long(1L))
                .description(Map.of("en", "A description"))
                .createdAt(dateTimeFactory.now())
                .build();
    }

    @Test
    void ensure_that_mapping_to_dto_works_properly() {
        var dto = mapper.toDto(content);
        
        assertThat(dto).isNotNull();
        assertThat(dto).extracting(ContentDto::getDescription)
                .asInstanceOf(InstanceOfAssertFactories.map(String.class, String.class))
                .contains(entry("en", "A description"));
    }

}