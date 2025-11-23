package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.PointOfInterest;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RemovePointOfInterestCommand(@NotNull PointOfInterest.PointOfInterestId poiId) {
}
