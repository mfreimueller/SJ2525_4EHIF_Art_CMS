package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.PointOfInterest;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.ZonedDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PointOfInterestRepositoryTest {

    private @Autowired PointOfInterestRepository pointOfInterestRepository;

    @Test
    void can_save_and_reread() {
        var creator = Creator.builder()
                .username("admin")
                .build();

        var poi = PointOfInterest.builder()
                .name("Mona Lisa")
                .createdAt(ZonedDateTime.now())
                .createdBy(creator)
                .build();

        var saved = pointOfInterestRepository.save(poi);

        assertThat(saved).extracting(PointOfInterest::getId).isNotNull();
        assertThat(saved).extracting(PointOfInterest::getCreatedBy).extracting(Creator::getId).isNotNull();
    }

}