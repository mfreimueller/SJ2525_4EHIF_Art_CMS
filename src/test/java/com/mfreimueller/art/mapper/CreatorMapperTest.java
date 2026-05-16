package com.mfreimueller.art.mapper;

import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.dto.CreatorDto;
import com.mfreimueller.art.mappers.CreatorMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CreatorMapperTest {

    @Autowired
    private CreatorMapper mapper;

    private Creator creator;

    @BeforeEach
    void setUp() {
        creator = Creator.builder()
                .id(new Creator.CreatorId(1L))
                .username("idefix")
                .password("secret")
                .role(Creator.Role.EDITOR)
                .build();
    }

    @Test
    void ensure_that_mapping_to_dto_works_properly() {
        var dto = mapper.toDto(creator);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(creator.getId());
        assertThat(dto.username()).isEqualTo("idefix");
        assertThat(dto.role()).isEqualTo(Creator.Role.EDITOR);
    }
}
