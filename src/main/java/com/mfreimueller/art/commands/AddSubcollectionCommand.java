package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.domain.PointOfInterest;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AddSubcollectionCommand(@NotNull Collection.CollectionId subcollectionId) {
}
