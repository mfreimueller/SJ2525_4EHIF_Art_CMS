package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.CreateExhibitionCommand;
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

        var cmd =  CreateExhibitionCommand.builder()
                .title(Map.of(en, "Dauerausstellung"))
                .languages(Set.of(en))
                .build();

        when(dateTimeFactory.now()).thenReturn(ZonedDateTime.of(2025, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault()));
        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var exhibition = service.create(cmd);

        assertNotNull(exhibition);
        verify(repository, times(1)).save(any());
    }

}