package com.mfreimueller.art.service;

import com.mfreimueller.art.domain.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

public final class ServiceFixtures {

    public static Creator createCreator() {
        return Creator.builder()
                .id(new Creator.CreatorId(1L))
                .username("editor")
                .role(Creator.Role.Editor)
                .build();
    }

    public static Collection createCollection() {
        var en = new Language("en", "English");
        var title = Map.of(en, "Dauerausstellung");

        return Collection.builder()
                .id(new Collection.CollectionId(1L))
                .title(title)
                .build();
    }

    public static Collection createSubcollection() {
        var en = new Language("en", "English");
        var title = Map.of(en, "Italian Artists");

        return Collection.builder()
                .id(new Collection.CollectionId(2L))
                .title(title)
                .build();
    }

    public static PointOfInterest createPointOfInterest() {
        var en = new Language("en", "English");
        var title = Map.of(en, "Girl with Balloon");
        var description = Map.of(en, "Girl with Balloon (also, Balloon Girl or Girl and Balloon) is a series of stencil murals around London by the graffiti artist Banksy, started in 2002. They depict a young girl with her hand extended toward a red heart-shaped balloon carried away by the wind.");

        return PointOfInterest.builder()
                .id(new PointOfInterest.PointOfInterestId(1L))
                .title(title)
                .description(description)
                .build();
    }

    public static Exhibition createExhibition() {
        var en = new Language("en", "English");
        var title = Map.of(en, "Dauerausstellung");

        return Exhibition.builder()
                .id(new Collection.CollectionId(1L))
                .title(title)
                .languages(Set.of(en))
                .build();
    }

    public static ZonedDateTime createDateTime() {
        return ZonedDateTime.of(2025, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault());
    }

    private ServiceFixtures() {}

}
