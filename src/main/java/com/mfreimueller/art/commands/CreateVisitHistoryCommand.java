package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.domain.Visitor;
import com.mfreimueller.art.richtypes.Duration;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.List;

@Builder
public record CreateVisitHistoryCommand(
        @NotNull List<PointOfInterest> pointsOfInterest,
        @NotNull Duration duration,
        @NotNull @PastOrPresent ZonedDateTime visitedOn,
        @NotNull Visitor.VisitorId visitorId) {
}
