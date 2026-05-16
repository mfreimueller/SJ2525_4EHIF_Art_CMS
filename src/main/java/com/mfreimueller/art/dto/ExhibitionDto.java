package com.mfreimueller.art.dto;

import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.domain.PointOfInterest;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@SuperBuilder

@AllArgsConstructor
@NoArgsConstructor
public class ExhibitionDto extends CollectionDto {
    private Map<String, String> title;
    @Builder.Default
    private Set<PointOfInterest.PointOfInterestId> pointsOfInterest = new HashSet<>();
    @Builder.Default
    private Set<Collection.CollectionId> subCollections = new HashSet<>();
    private Collection.CollectionId parentCollection;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private CreatorDto createdBy;
    private CreatorDto updatedBy;
    @Builder.Default
    private Set<String> languages = new HashSet<>();
}
