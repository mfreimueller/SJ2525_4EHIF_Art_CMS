package com.mfreimueller.art.dto;

import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.domain.VisitHistory;
import com.mfreimueller.art.domain.Visitor;
import com.mfreimueller.art.richtypes.Duration;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.List;

@Builder
public record VisitHistoryDto(
        VisitHistory.VisitHistoryId id,
        Duration duration,
        ZonedDateTime visitedOn,
        List<PointOfInterest.PointOfInterestId> pointsOfInterest,
        Visitor.VisitorId visitor) {
}
