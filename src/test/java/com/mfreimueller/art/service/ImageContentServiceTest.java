package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.PutImageContentCommand;
import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.ImageContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.mfreimueller.art.service.ServiceFixtures.*;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class ImageContentServiceTest {

    private @InjectMocks ImageContentService service;
    private @Mock ImageContentRepository repository;
    private @Mock CreatorService creatorService;
    private @Mock DateTimeFactory dateTimeFactory;

    @BeforeEach
    public void setup() {
        assumeThat(service).isNotNull();
        assumeThat(repository).isNotNull();
        assumeThat(creatorService).isNotNull();
        assumeThat(dateTimeFactory).isNotNull();
    }

    @Test
    public void can_create_with_valid_data() {
        var creator = createCreator();
        var dateTime = dateTime();

        var description = localizedText();
        var source = source();

        var cmd = PutImageContentCommand.builder()
                .description(description)
                .source(source)
                .creatorId(creator.getId())
                .build();

        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(creatorService.getByReference(any())).thenReturn(creator);
        when(dateTimeFactory.now()).thenReturn(dateTime);

        var textContent = service.create(cmd);

        assertNotNull(textContent);
        assertEquals(textContent.getDescription(), description);
        assertEquals(textContent.getSource(), source);
        assertThat(textContent.getCreatedBy(), equalTo(creator));
        assertThat(textContent.getCreatedAt(), equalTo(dateTime));

        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_update_existing_entity() {
        var creator = createCreator();
        var dateTime = dateTime();

        var imageContent = createImageContent();
        var source = source();

        var cmd = PutImageContentCommand.builder()
                .source(source)
                .build();

        when(repository.getReferenceById(any())).thenReturn(imageContent);
        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(creatorService.getByReference(any())).thenReturn(creator);
        when(dateTimeFactory.now()).thenReturn(dateTime);

        var returned = service.update(imageContent.getId(), cmd);

        assertNotNull(returned);
        assertThat(returned.getSource(), equalTo(source));
        assertThat(imageContent.getUpdatedBy(), equalTo(creator));
        assertThat(imageContent.getUpdatedAt(), equalTo(dateTime));

        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_delete_existing_entity() {
        service.delete(new Content.ContentId(1L));
        verify(repository, times(1)).deleteById(any());
    }

    @Test
    public void returns_existing_entity() {
        var imageContent = createImageContent();
        when(repository.getReferenceById(any())).thenReturn(imageContent);

        var returned = service.getByReference(new Content.ContentId(1L));

        assertNotNull(returned);
        assertThat(returned.getId(), equalTo(imageContent.getId()));

        verify(repository, times(1)).getReferenceById(any());
    }



}