package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.richtypes.Language;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.ZonedDateTime;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ContentRepositoryTest {

    private @Autowired ContentRepository contentRepository;

    @Test
    void can_save_and_reread() {
        var creator = Creator.builder()
                .username("admin")
                .build();

        var languageEn = new Language("en");
        var shortTextEn = "This famous painting by Leonardo da Vinci...";

        var content = Content.builder()
                .shortText(Map.of(languageEn, shortTextEn))
                .createdAt(ZonedDateTime.now())
                .createdBy(creator)
                .build();

        var saved = contentRepository.save(content);

        assertThat(saved).extracting(Content::getId).isNotNull();
        assertThat(saved).extracting(Content::getCreatedBy).extracting(Creator::getId).isNotNull();
    }

}