package com.mfreimueller.art.dto;

import com.mfreimueller.art.domain.Language;
import com.mfreimueller.art.domain.PointOfInterest;

import java.util.Map;

public record PointOfInterestDto(PointOfInterest.PointOfInterestId id, Map<Language, String> title) {

    public PointOfInterestDto(PointOfInterest poi) {
        this(poi.getId(), poi.getTitle());
    }

}
