package com.mfreimueller.art.persistence;

import com.mfreimueller.art.TestcontainersConfiguration;
import com.mfreimueller.art.domain.*;
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
class ContentRepositoryTest {

    private @Autowired AudioContentRepository audioContentRepository;
    private @Autowired ContentRepository contentRepository;

    @Test
    void can_save_and_reread() {
        var creator = Creator.builder()
                .username("admin")
                .build();

        var sourceEn = new Source("https://google.com/...", Source.LinkType.Url);
        var languageEn = new Language("en", "English");

        var content = AudioContent.builder()
                .source(Map.of(languageEn, sourceEn))
                .createdAt(ZonedDateTime.now())
                .createdBy(creator)
                .build();

        var saved = audioContentRepository.save(content);
        var returned = contentRepository.getReferenceById(saved.getId());

        assertThat(returned).extracting(Content::getId).isNotNull();
        assertThat(returned).extracting(Content::getCreatedBy).extracting(Creator::getId).isNotNull();
    }

}