package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.PointOfInterest;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

@Builder
public record AddPointOfInterestCommand(@NotNull PointOfInterest.PointOfInterestId poiId) {
}
