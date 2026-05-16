package com.mfreimueller.art.mapper;

import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.domain.VisitHistory;
import com.mfreimueller.art.domain.Visitor;
import com.mfreimueller.art.dto.VisitHistoryDto;
import com.mfreimueller.art.mappers.VisitHistoryMapper;
import com.mfreimueller.art.richtypes.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VisitHistoryMapperTest {

    @Autowired
    private VisitHistoryMapper mapper;

    private VisitHistory visitHistory;

    @BeforeEach
    void setUp() {
        var poi = PointOfInterest.builder()
                .id(new PointOfInterest.PointOfInterestId(1L))
                .build();
        var visitor = Visitor.builder()
                .id(new Visitor.VisitorId(1L))
                .username("john_doe")
                .emailAddress("john@example.com")
                .build();

        visitHistory = VisitHistory.builder()
                .id(new VisitHistory.VisitHistoryId(1L))
                .duration(Duration.of(30))
                .visitedOn(ZonedDateTime.of(2025, 1, 15, 10, 30, 0, 0, ZoneId.systemDefault()))
                .pointsOfInterest(List.of(poi))
                .visitor(visitor)
                .build();
    }

    @Test
    void ensure_that_mapping_to_dto_works_properly() {
        var dto = mapper.toDto(visitHistory);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(visitHistory.getId());
        assertThat(dto.duration()).isEqualTo(Duration.of(30));
        assertThat(dto.visitedOn()).isEqualTo(visitHistory.getVisitedOn());
        assertThat(dto.pointsOfInterest())
                .hasSize(1)
                .contains(new PointOfInterest.PointOfInterestId(1L));
        assertThat(dto.visitor()).isEqualTo(new Visitor.VisitorId(1L));
    }
}
