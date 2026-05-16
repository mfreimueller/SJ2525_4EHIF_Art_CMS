package com.mfreimueller.art.mapper;

import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.dto.CollectionDto;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.mappers.CollectionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CollectionMapperTest {

    @Autowired
    private DateTimeFactory dateTimeFactory;

    @Autowired
    private CollectionMapper mapper;

    private Collection collection;

    @BeforeEach
    void setUp() {
        var poi = PointOfInterest.builder()
                .id(new PointOfInterest.PointOfInterestId(1L))
                .title(Map.of("en", "A POI"))
                .createdAt(dateTimeFactory.now())
                .build();
        var subcollection = Collection.builder()
                .id(new Collection.CollectionId(2L))
                .title(Map.of("en", "Subcollection"))
                .pointsOfInterest(new HashSet<>())
                .subCollections(new HashSet<>())
                .createdAt(dateTimeFactory.now())
                .build();
        var creator = Creator.builder()
                .id(new Creator.CreatorId(1L))
                .username("admin")
                .password("secret")
                .role(Creator.Role.ADMIN)
                .build();

        collection = Collection.builder()
                .id(new Collection.CollectionId(1L))
                .title(Map.of("en", "Main Collection"))
                .pointsOfInterest(Set.of(poi))
                .subCollections(Set.of(subcollection))
                .createdAt(dateTimeFactory.now())
                .createdBy(creator)
                .updatedBy(creator)
                .build();
    }

    @Test
    void ensure_that_mapping_to_dto_works_properly() {
        var dto = mapper.toDto(collection);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(collection.getId());
        assertThat(dto.getTitle()).contains(entry("en", "Main Collection"));
        assertThat(dto.getPointsOfInterest())
                .hasSize(1)
                .contains(new PointOfInterest.PointOfInterestId(1L));
        assertThat(dto.getSubCollections())
                .hasSize(1)
                .contains(new Collection.CollectionId(2L));
        assertThat(dto.getCreatedBy()).isNotNull();
        assertThat(dto.getCreatedBy().username()).isEqualTo("admin");
    }
}
