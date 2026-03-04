package com.mfreimueller.art.dto;

import com.mfreimueller.art.domain.PointOfInterest;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
public record PointOfInterestDto(
        PointOfInterest.PointOfInterestId id,
        Map<String, String> title,
        Map<String, String> description,
        List<ContentDto> content) {

    // note: because we are using a record, we cannot use @Builder.Default, instead we provide a custom
    // builder class and set our default values
    public static class PointOfInterestDtoBuilder {
        PointOfInterestDtoBuilder() {
            description = new HashMap<>();
            content = new ArrayList<>();
        }

    }

}
