package com.mfreimueller.art.mapper;

import com.mfreimueller.art.domain.Visitor;
import com.mfreimueller.art.dto.VisitorDto;
import com.mfreimueller.art.mappers.VisitorMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VisitorMapperTest {

    @Autowired
    private VisitorMapper mapper;

    private Visitor visitor;

    @BeforeEach
    void setUp() {
        visitor = Visitor.builder()
                .id(new Visitor.VisitorId(1L))
                .username("john_doe")
                .emailAddress("john@example.com")
                .build();
    }

    @Test
    void ensure_that_mapping_to_dto_works_properly() {
        var dto = mapper.toDto(visitor);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(visitor.getId());
        assertThat(dto.username()).isEqualTo("john_doe");
        assertThat(dto.emailAddress()).isEqualTo("john@example.com");
    }
}
