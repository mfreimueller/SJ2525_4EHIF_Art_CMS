package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.CreatePointOfInterestCommand;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
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

        assertThat(poi).isNotNull();
        verify(repository, times(1)).save(any());
    }
}