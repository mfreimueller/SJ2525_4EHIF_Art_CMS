package com.mfreimueller.art.persistence;

import com.mfreimueller.art.TestcontainersConfiguration;
import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.domain.Language;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.ZonedDateTime;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Import(TestcontainersConfiguration.class)
class PointOfInterestRepositoryTest {

    private @Autowired PointOfInterestRepository pointOfInterestRepository;

    @Test
    void can_save_and_reread() {
        var creator = Creator.builder()
                .username("admin")
                .build();

        var languageEn = new Language("en", "English");
        var titleEn = "Mona Lisa";

        var poi = PointOfInterest.builder()
                .title(Map.of(languageEn, titleEn))
                .createdAt(ZonedDateTime.now())
                .createdBy(creator)
                .build();

        var saved = pointOfInterestRepository.save(poi);

        assertThat(saved).extracting(PointOfInterest::getId).isNotNull();
        assertThat(saved).extracting(PointOfInterest::getCreatedBy).extracting(Creator::getId).isNotNull();
    }

}