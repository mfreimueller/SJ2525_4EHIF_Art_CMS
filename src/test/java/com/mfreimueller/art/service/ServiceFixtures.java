package com.mfreimueller.art.service;

import com.github.javafaker.Faker;
import com.mfreimueller.art.domain.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class ServiceFixtures {
    private static final Faker faker = new Faker();

    public static Map<Language, String> localizedText() {
        return Map.of(
                new Language("en", "English"), faker.witcher().quote(),
                new Language("de", "Deutsch"), faker.gameOfThrones().quote()
        );
    }

    public static Creator createCreator() {
        return Creator.builder()
                .id(new Creator.CreatorId(randomId()))
                .username("editor")
                .role(Creator.Role.Editor)
                .build();
    }

    public static Visitor createVisitor() {
        return Visitor.builder()
                .id(new Visitor.VisitorId(randomId()))
                .username(faker.name().username())
                .emailAddress(validEmail())
                .build();
    }

    public static Collection createCollection() {
        var en = new Language("en", "English");
        var title = Map.of(en, faker.witcher().location());

        return Collection.builder()
                .id(new Collection.CollectionId(randomId()))
                .title(title)
                .build();
    }

    public static Collection createSubcollection() {
        var en = new Language("en", "English");
        var title = Map.of(en, faker.lordOfTheRings().location());

        return Collection.builder()
                .id(new Collection.CollectionId(randomId()))
                .title(title)
                .build();
    }

    public static PointOfInterest createPointOfInterest() {
        var en = new Language("en", "English");
        var title = Map.of(en, "Girl with Balloon");
        var description = Map.of(en, "Girl with Balloon (also, Balloon Girl or Girl and Balloon) is a series of stencil murals around London by the graffiti artist Banksy, started in 2002. They depict a young girl with her hand extended toward a red heart-shaped balloon carried away by the wind.");

        return PointOfInterest.builder()
                .id(new PointOfInterest.PointOfInterestId(randomId()))
                .title(title)
                .description(description)
                .build();
    }

    public static Exhibition createExhibition() {
        var en = new Language("en", "English");
        var title = Map.of(en, faker.elderScrolls().city());

        return Exhibition.builder()
                .id(new Collection.CollectionId(randomId()))
                .title(title)
                .languages(Set.of(en))
                .build();
    }

    public static TextContent createTextContent() {
        var description = localizedText();
        var shortText = localizedText();
        var longText = localizedText();

        return TextContent.builder()
                .id(new Content.ContentId(randomId()))
                .description(description)
                .shortText(shortText)
                .longText(longText)
                .build();
    }

    public static String validEmail() {
        return faker.bothify("????##@gmail.com");
    }

    public static Long randomId() {
        return faker.random().nextLong(Long.MAX_VALUE);
    }

    public static ZonedDateTime dateTime() {
        return ZonedDateTime.of(2025, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault());
    }

    public static ZonedDateTime createPastDateTime() {
        return faker.date().past(24, TimeUnit.HOURS).toInstant().atZone(ZoneId.systemDefault());
    }

    private ServiceFixtures() {}

}
