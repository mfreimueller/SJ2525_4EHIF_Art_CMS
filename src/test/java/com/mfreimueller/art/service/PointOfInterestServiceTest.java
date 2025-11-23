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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

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
    private @Mock DateTimeFactory dateTimeFactory;

    @BeforeEach
    void setUp() {
        assumeThat(service).isNotNull();
        assumeThat(repository).isNotNull();
        assumeThat(dateTimeFactory).isNotNull();
    }

    @Test
    void can_create_with_valid_data() {
        var en = new Language("en", "English");

        var cmd = CreatePointOfInterestCommand.builder()
                .title(Map.of(en, "Mona Lisa"))
                .description(Map.of(en, "Portrait painting of Lisa del Giocondo, oil on poplar wood"))
                .build();

        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(dateTimeFactory.now()).thenReturn(ZonedDateTime.of(2025, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()));

        var poi = service.create(cmd);

        assertThat(poi, notNullValue());
        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_update_existing_entity() {
        var exhibition = createPointOfInterest();
        var de = new Language("de", "Deutsch");

        var cmd = UpdatePointOfInterestCommand.builder()
                .title(Map.of(de, "Mädchen mit Balloon"))
                .description(Map.of())
                .build();

        var dateTime = ZonedDateTime.of(2025, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault());

        when(repository.getReferenceById(any())).thenReturn(exhibition);
        when(dateTimeFactory.now()).thenReturn(dateTime);
        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var updateExhibition = service.update(exhibition.getId(), cmd);

        assertThat(updateExhibition, notNullValue());
        assertThat(updateExhibition.getUpdatedAt(), equalTo(dateTime));
        assertThat(updateExhibition.getTitle(), hasKey(de));
        assertEquals(0, updateExhibition.getDescription().size());

        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_delete_existing_entity() {
        service.delete(new PointOfInterest.PointOfInterestId(1L));
        verify(repository, times(1)).deleteById(any());
    }

    @Test
    public void returns_existing_entity() {
        var exhibition = createPointOfInterest();
        when(repository.getReferenceById(any())).thenReturn(exhibition);

        var returned = service.getByReference(new PointOfInterest.PointOfInterestId(1L));

        assertThat(returned, notNullValue());
        assertThat(returned.getId(), equalTo(exhibition.getId()));

        verify(repository, times(1)).getReferenceById(any());
    }

    private PointOfInterest createPointOfInterest() {
        var en = new Language("en", "English");
        var title = Map.of(en, "Girl with Balloon");
        var description = Map.of(en, "Girl with Balloon (also, Balloon Girl or Girl and Balloon) is a series of stencil murals around London by the graffiti artist Banksy, started in 2002. They depict a young girl with her hand extended toward a red heart-shaped balloon carried away by the wind.");

        return PointOfInterest.builder()
                .id(new PointOfInterest.PointOfInterestId(1L))
                .title(title)
                .description(description)
                .build();
    }
}