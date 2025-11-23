package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.CreateUpdateTextContentCommand;
import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.persistence.TextContentRepository;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class TextContentServiceTest {

    private @InjectMocks TextContentService service;
    private @Mock TextContentRepository repository;

    @BeforeEach
    public void setup() {
        assumeThat(service).isNotNull();
        assumeThat(repository).isNotNull();
    }

    @Test
    public void can_create_with_valid_data() {
        var description = localizedText();
        var shortText = localizedText();
        var longText = localizedText();

        var cmd = CreateUpdateTextContentCommand.builder()
                .description(description)
                .shortText(shortText)
                .longText(longText)
                .build();

        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var textContent = service.create(cmd);

        assertNotNull(textContent);
        assertEquals(textContent.getDescription(), description);
        assertEquals(textContent.getShortText(), shortText);
        assertEquals(textContent.getLongText(), longText);

        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_update_existing_entity() {
        var textContent = createTextContent();

        var shortText = localizedText();

        var cmd = CreateUpdateTextContentCommand.builder()
                .shortText(shortText)
                .build();

        when(repository.getReferenceById(any())).thenReturn(textContent);
        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var returned = service.update(textContent.getId(), cmd);

        assertNotNull(returned);
        assertThat(returned.getShortText(), equalTo(shortText));

        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_delete_existing_entity() {
        service.delete(new Content.ContentId(1L));
        verify(repository, times(1)).deleteById(any());
    }

    @Test
    public void returns_existing_entity() {
        var textContent = createTextContent();
        when(repository.getReferenceById(any())).thenReturn(textContent);

        var returned = service.getByReference(new Content.ContentId(1L));

        assertNotNull(returned);
        assertThat(returned.getId(), equalTo(textContent.getId()));

        verify(repository, times(1)).getReferenceById(any());
    }



}