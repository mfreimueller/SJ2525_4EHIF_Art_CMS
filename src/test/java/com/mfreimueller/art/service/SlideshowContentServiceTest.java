package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.PutSlideshowContentCommand;
import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.domain.SlideshowContent;
import com.mfreimueller.art.foundation.DataConstraintException;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.ContentRepository;
import com.mfreimueller.art.persistence.SlideshowContentRepository;
import com.mfreimueller.art.richtypes.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.mfreimueller.art.service.ServiceFixtures.*;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class SlideshowContentServiceTest {

    private @InjectMocks SlideshowContentService service;
    private @Mock SlideshowContentRepository repository;
    private @Mock ContentRepository contentRepository;
    private @Mock CreatorService creatorService;
    private @Mock DateTimeFactory dateTimeFactory;

    @BeforeEach
    public void setup() {
        assumeThat(service).isNotNull();
        assumeThat(repository).isNotNull();
        assumeThat(contentRepository).isNotNull();
        assumeThat(creatorService).isNotNull();
        assumeThat(dateTimeFactory).isNotNull();
    }

    @Test
    public void can_create_with_valid_data() {
        var description = localizedText();

        var cmd = PutSlideshowContentCommand.builder()
                .description(description)
                .mode(SlideshowContent.Mode.Auto)
                .speed(Duration.of(5))
                .slides(List.of())
                .build();

        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var slideshowContent = service.create(cmd);

        assertNotNull(slideshowContent);
        assertEquals(description, slideshowContent.getDescription());
        assertEquals(SlideshowContent.Mode.Auto, slideshowContent.getMode());
        assertEquals(5, slideshowContent.getSpeed().value());

        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_update_existing_entity() {
        var slideshowContent = slideshowContent();

        var description = localizedText();

        var cmd = PutSlideshowContentCommand.builder()
                .description(description)
                .slides(List.of())
                .build();

        when(repository.getReferenceById(any())).thenReturn(slideshowContent);
        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var returned = service.update(slideshowContent.getId(), cmd);

        assertNotNull(returned);
        assertThat(returned.getDescription(), equalTo(description));

        verify(repository, times(1)).save(any());
    }

    @Test
    public void detects_circular_references() {
        var slideshowContent = slideshowContent();

        var slides = new ArrayList<>(slideshowContent.getSlides());
        slides.add(slideshowContent);

        var cmd = PutSlideshowContentCommand.builder()
                .slides(slides.stream().map(Content::getId).toList())
                .build();

        when(repository.getReferenceById(any())).thenReturn(slideshowContent);

        for (Content c : slides) {
            when(contentRepository.getReferenceById(c.getId())).thenReturn(c);
        }

        assertThrows(DataConstraintException.class, () -> service.update(slideshowContent.getId(), cmd));
    }

    @Test
    public void can_delete_existing_entity() {
        service.delete(new Content.ContentId(1L));
        verify(repository, times(1)).deleteById(any());
    }

    @Test
    public void returns_existing_entity() {
        var slideshowContent = slideshowContent();
        when(repository.getReferenceById(any())).thenReturn(slideshowContent);

        var returned = service.getByReference(new Content.ContentId(1L));

        assertNotNull(returned);
        assertThat(returned.getId(), equalTo(slideshowContent.getId()));

        verify(repository, times(1)).getReferenceById(any());
    }


}