package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.domain.Language;
import com.mfreimueller.art.domain.PointOfInterest;
import lombok.Builder;

import java.util.Map;
import java.util.Set;

@Builder
public record CreateExhibitionCommand(Map<Language, String> title,
                                      Set<PointOfInterest> pointsOfInterests, Set<Collection> subCollections,
                                      Set<Language> languages) {
}
