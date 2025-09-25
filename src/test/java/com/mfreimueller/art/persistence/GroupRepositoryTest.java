package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.Group;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.richtypes.Language;
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
class GroupRepositoryTest {

    private @Autowired GroupRepository groupRepository;

    @Test
    void can_save_and_reread() {
        var creator = Creator.builder()
                .username("admin")
                .build();

        var languageEn = new Language("en");
        var titleEn = "Mona Lisa";

        var poi = PointOfInterest.builder()
                .title(Map.of(languageEn, titleEn))
                .build();

        var group = Group.builder()
                .createdBy(creator)
                .pointsOfInterest(Set.of(poi))
                .build();

        var saved = groupRepository.save(group);

        assertThat(saved).extracting(Group::getId).isNotNull();
        assertThat(saved).extracting(Group::getCreatedBy).extracting(Creator::getId).isNotNull();
        assertThat(saved).extracting(Group::getPointsOfInterest)
                .asInstanceOf(set(PointOfInterest.class))
                .anySatisfy(p -> {
                    assertThat(p)
                            .extracting(PointOfInterest::getId)
                            .isNotNull();
                });
    }

}