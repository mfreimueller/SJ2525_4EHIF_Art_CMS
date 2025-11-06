package com.mfreimueller.art.persistence;

import com.mfreimueller.art.TestcontainersConfiguration;
import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.SlideshowContent;
import com.mfreimueller.art.domain.TextContent;
import com.mfreimueller.art.domain.Language;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Import(TestcontainersConfiguration.class)
class SlideshowContentRepositoryTest {

    private @Autowired SlideshowContentRepository slideshowContentRepository;

    @Test
    void can_save_and_reread() {
        var creator = Creator.builder()
                .username("admin")
                .build();

        var languageEn = new Language("en", "English");
        var shortTextEn = "This famous painting by Leonardo da Vinci...";

        var textContent = TextContent.builder()
                .shortText(Map.of(languageEn, shortTextEn))
                .build();

        var content = SlideshowContent.builder()
                .slides(List.of(textContent))
                .createdAt(ZonedDateTime.now())
                .createdBy(creator)
                .build();

        var saved = slideshowContentRepository.save(content);

        assertThat(saved).extracting(SlideshowContent::getId).isNotNull();
        assertThat(saved).extracting(SlideshowContent::getCreatedBy).extracting(Creator::getId).isNotNull();
        assertThat(saved).extracting(SlideshowContent::getSlides)
                .asInstanceOf(list(Content.class))
                .anySatisfy(slide -> assertThat(slide).extracting(Content::getId).isNotNull());
    }

}