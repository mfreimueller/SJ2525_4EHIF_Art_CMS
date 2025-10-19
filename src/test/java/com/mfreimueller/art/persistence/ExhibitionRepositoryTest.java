package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.Exhibition;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.domain.Language;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.set;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ExhibitionRepositoryTest {

    private @Autowired ExhibitionRepository exhibitionRepository;

    @Test
    void can_save_and_reread() {
        var creator = Creator.builder()
                .username("admin")
                .build();

        var languageEn = new Language("en", "English");
        var titleEn = "Mona Lisa";

        var poi = PointOfInterest.builder()
                .title(Map.of(languageEn, titleEn))
                .build();

        var exhibition = Exhibition.builder()
                .languages(Set.of(languageEn))
                .createdBy(creator)
                .pointsOfInterest(Set.of(poi))
                .build();

        var saved = exhibitionRepository.save(exhibition);

        assertThat(saved).extracting(Exhibition::getId).isNotNull();
        assertThat(saved).extracting(Exhibition::getCreatedBy).extracting(Creator::getId).isNotNull();
        assertThat(saved).extracting(Exhibition::getPointsOfInterest)
                .asInstanceOf(set(PointOfInterest.class))
                .anySatisfy(p -> assertThat(p)
                            .extracting(PointOfInterest::getId)
                            .isNotNull()
                );
    }

}