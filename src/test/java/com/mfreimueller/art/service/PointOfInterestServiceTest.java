package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.CreatePointOfInterestCommand;
import com.mfreimueller.art.commands.UpdatePointOfInterestCommand;
import com.mfreimueller.art.domain.Language;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.PointOfInterestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static com.mfreimueller.art.service.ServiceFixtures.*;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class PointOfInterestServiceTest {

    private @InjectMocks PointOfInterestService service;
    private @Mock PointOfInterestRepository repository;
    private @Mock CreatorService creatorService;
    private @Mock ContentService contentService;
    private @Mock DateTimeFactory dateTimeFactory;

    @BeforeEach
    void setUp() {
        assumeThat(service).isNotNull();
        assumeThat(repository).isNotNull();
        assumeThat(creatorService).isNotNull();
        assumeThat(contentService).isNotNull();
        assumeThat(dateTimeFactory).isNotNull();
    }

    @Test
    void can_create_with_valid_data() {
        var creator = creator();
        var en = new Language("en", "English");
        var dateTime = dateTime();

        var cmd = CreatePointOfInterestCommand.builder()
                .title(Map.of(en, "Mona Lisa"))
                .description(Map.of(en, "Portrait painting of Lisa del Giocondo, oil on poplar wood"))
                .content(List.of())
                .creatorId(creator.getId())
                .build();


        when(dateTimeFactory.now()).thenReturn(dateTime);
        when(creatorService.getByReference(any())).thenReturn(creator);

        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var poi = service.create(cmd);

        assertThat(poi, notNullValue());
        assertEquals(poi.getCreatedAt(), dateTime);
        assertThat(poi.getCreatedBy(), equalTo(creator));

        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_update_existing_entity() {
        var creator = creator();
        var exhibition = pointOfInterest();
        var de = new Language("de", "Deutsch");
        var dateTime = dateTime();

        var cmd = UpdatePointOfInterestCommand.builder()
                .title(Map.of(de, "Mädchen mit Balloon"))
                .description(Map.of())
                .content(List.of())
                .build();

        when(repository.getReferenceById(any())).thenReturn(exhibition);

        when(dateTimeFactory.now()).thenReturn(dateTime);
        when(creatorService.getByReference(any())).thenReturn(creator);

        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var updateExhibition = service.update(exhibition.getId(), cmd);

        assertThat(updateExhibition, notNullValue());
        assertThat(updateExhibition.getTitle(), hasKey(de));
        assertEquals(0, updateExhibition.getDescription().size());
        assertThat(updateExhibition.getUpdatedAt(), equalTo(dateTime));
        assertThat(exhibition.getUpdatedBy(), equalTo(creator));

        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_delete_existing_entity() {
        service.delete(new PointOfInterest.PointOfInterestId(1L));
        verify(repository, times(1)).deleteById(any());
    }

    @Test
    public void returns_existing_entity() {
        var exhibition = pointOfInterest();
        when(repository.getReferenceById(any())).thenReturn(exhibition);

        var returned = service.getByReference(new PointOfInterest.PointOfInterestId(1L));

        assertThat(returned, notNullValue());
        assertThat(returned.getId(), equalTo(exhibition.getId()));

        verify(repository, times(1)).getReferenceById(any());
    }
}