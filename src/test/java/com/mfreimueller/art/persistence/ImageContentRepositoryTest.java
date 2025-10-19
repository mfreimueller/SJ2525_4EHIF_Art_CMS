package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.ImageContent;
import com.mfreimueller.art.domain.Source;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.ZonedDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ImageContentRepositoryTest {

    private @Autowired ImageContentRepository imageContentRepository;

    @Test
    void can_save_and_reread() {
        var creator = Creator.builder()
                .username("admin")
                .build();

        var source = new Source("https://google.com/...", Source.LinkType.Url);

        var content = ImageContent.builder()
                .source(source)
                .createdAt(ZonedDateTime.now())
                .createdBy(creator)
                .build();

        var saved = imageContentRepository.save(content);

        assertThat(saved).extracting(ImageContent::getId).isNotNull();
        assertThat(saved).extracting(ImageContent::getCreatedBy).extracting(Creator::getId).isNotNull();
    }

}