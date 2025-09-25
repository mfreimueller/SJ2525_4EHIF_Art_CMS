package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.AudioContent;
import com.mfreimueller.art.richtypes.Language;
import com.mfreimueller.art.richtypes.Source;
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
class AudioContentRepositoryTest {

    private @Autowired AudioContentRepository audioContentRepository;

    @Test
    void can_save_and_reread() {
        var creator = Creator.builder()
                .username("admin")
                .build();

        var sourceEn = new Source("https://google.com/...", Source.LinkType.Url);
        var languageEn = new Language("en");

        var content = AudioContent.builder()
                .source(Map.of(languageEn, sourceEn))
                .createdAt(ZonedDateTime.now())
                .createdBy(creator)
                .build();

        var saved = audioContentRepository.save(content);

        assertThat(saved).extracting(AudioContent::getId).isNotNull();
        assertThat(saved).extracting(AudioContent::getCreatedBy).extracting(Creator::getId).isNotNull();
    }

}