package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.PutAudioContentCommand;
import com.mfreimueller.art.commands.PutTextContentCommand;
import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.AudioContentRepository;
import com.mfreimueller.art.persistence.TextContentRepository;
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
class AudioContentServiceTest {

    private @InjectMocks AudioContentService service;
    private @Mock AudioContentRepository repository;
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
        var sources = localizedSources();
        var transcriptions = localizedSources();
        var duration = localizedDurations();

        var cmd = PutAudioContentCommand.builder()
                .description(description)
                .duration(duration)
                .source(sources)
                .transcriptions(transcriptions)
                .creatorId(creator.getId())
                .build();

        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(creatorService.getByReference(any())).thenReturn(creator);
        when(dateTimeFactory.now()).thenReturn(dateTime);

        var textContent = service.create(cmd);

        assertNotNull(textContent);
        assertEquals(description, textContent.getDescription());
        assertEquals(transcriptions, textContent.getTranscriptions());
        assertEquals(sources, textContent.getSource());
        assertThat(textContent.getCreatedBy(), equalTo(creator));
        assertThat(textContent.getCreatedAt(), equalTo(dateTime));

        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_update_existing_entity() {
        var creator = createCreator();
        var dateTime = dateTime();

        var audioContent = audioContent();

        var transcriptions = localizedSources();

        var cmd = PutAudioContentCommand.builder()
                .transcriptions(transcriptions)
                .build();

        when(repository.getReferenceById(any())).thenReturn(audioContent);
        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(creatorService.getByReference(any())).thenReturn(creator);
        when(dateTimeFactory.now()).thenReturn(dateTime);

        var returned = service.update(audioContent.getId(), cmd);

        assertNotNull(returned);
        assertThat(returned.getTranscriptions(), equalTo(transcriptions));
        assertThat(returned.getUpdatedBy(), equalTo(creator));
        assertThat(returned.getUpdatedAt(), equalTo(dateTime));

        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_delete_existing_entity() {
        service.delete(new Content.ContentId(1L));
        verify(repository, times(1)).deleteById(any());
    }

    @Test
    public void returns_existing_entity() {
        var audioContent = audioContent();
        when(repository.getReferenceById(any())).thenReturn(audioContent);

        var returned = service.getByReference(new Content.ContentId(1L));

        assertNotNull(returned);
        assertThat(returned.getId(), equalTo(audioContent.getId()));

        verify(repository, times(1)).getReferenceById(any());
    }



}