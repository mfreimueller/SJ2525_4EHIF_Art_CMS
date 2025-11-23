package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.CreateExhibitionCommand;
import com.mfreimueller.art.commands.UpdateExhibitionCommand;
import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.domain.Exhibition;
import com.mfreimueller.art.domain.Language;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.ExhibitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assumptions.assumeThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class ExhibitionServiceTest {

    private @InjectMocks ExhibitionService service;
    private @Mock ExhibitionRepository repository;
    private @Mock DateTimeFactory dateTimeFactory;

    @BeforeEach
    public void setup() {
        assumeThat(service).isNotNull();
        assumeThat(repository).isNotNull();
        assumeThat(dateTimeFactory).isNotNull();
    }

    @Test
    public void can_create_with_valid_data() {
        var en = new Language("en", "English");

        var cmd = CreateExhibitionCommand.builder()
                .title(Map.of(en, "Dauerausstellung"))
                .languages(Set.of(en))
                .build();

        when(dateTimeFactory.now()).thenReturn(ZonedDateTime.of(2025, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()));
        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var exhibition = service.create(cmd);

        assertNotNull(exhibition);
        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_update_existing_entity() {
        var exhibition = createExhibition();
        var de = new Language("de", "Deutsch");

        var cmd = UpdateExhibitionCommand.builder()
                .title(Map.of(de, "Dauerausstellung"))
                .languages(Set.of(de))
                .build();

        var dateTime = ZonedDateTime.of(2025, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault());

        when(repository.getReferenceById(any())).thenReturn(exhibition);
        when(dateTimeFactory.now()).thenReturn(dateTime);
        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var updateExhibition = service.update(exhibition.getId(), cmd);

        assertNotNull(updateExhibition);
        assertEquals(updateExhibition.getUpdatedAt(), dateTime);
        assertTrue(updateExhibition.getTitle().containsKey(de));
        assertEquals(1, updateExhibition.getLanguages().size());
        assertThat(updateExhibition.getLanguages(), hasItem(de));

        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_delete_existing_entity() {
        service.delete(new Collection.CollectionId(1L));
        verify(repository, times(1)).deleteById(any());
    }

    @Test
    public void returns_existing_entity() {
        var exhibition = createExhibition();
        when(repository.getReferenceById(any())).thenReturn(exhibition);

        var returned = service.getByReference(new Collection.CollectionId(1L));

        assertNotNull(returned);
        assertThat(returned.getId(), equalTo(exhibition.getId()));

        verify(repository, times(1)).getReferenceById(any());
    }

    private Exhibition createExhibition() {
        var en = new Language("en", "English");
        var title = Map.of(en, "Dauerausstellung");

        return Exhibition.builder()
                .id(new Collection.CollectionId(1L))
                .title(title)
                .languages(Set.of(en))
                .build();
    }

}