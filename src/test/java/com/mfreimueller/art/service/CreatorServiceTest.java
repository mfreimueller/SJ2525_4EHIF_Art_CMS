package com.mfreimueller.art.service;

import com.mfreimueller.art.commands.CreateCreatorCommand;
import com.mfreimueller.art.commands.CreateExhibitionCommand;
import com.mfreimueller.art.commands.UpdateCreatorCommand;
import com.mfreimueller.art.commands.UpdateExhibitionCommand;
import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.Exhibition;
import com.mfreimueller.art.domain.Language;
import com.mfreimueller.art.foundation.DateTimeFactory;
import com.mfreimueller.art.persistence.CreatorRepository;
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
class CreatorServiceTest {

    private @InjectMocks CreatorService service;
    private @Mock CreatorRepository repository;

    @BeforeEach
    public void setup() {
        assumeThat(service).isNotNull();
        assumeThat(repository).isNotNull();
    }

    @Test
    public void can_create_with_valid_data() {
        var cmd = CreateCreatorCommand.builder()
                .username("admin") // security best practice
                .role(Creator.Role.Admin)
                .build();

        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var creator = service.create(cmd);

        assertNotNull(creator);
        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_update_existing_entity() {
        var creator = createCreator();

        var cmd = UpdateCreatorCommand.builder()
                .username("guest")
                .role(Creator.Role.Viewer)
                .build();

        when(repository.getReferenceById(any())).thenReturn(creator);
        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var returned = service.update(creator.getId(), cmd);

        assertNotNull(returned);
        assertThat(returned.getUsername(), equalTo("guest"));
        assertThat(returned.getRole(), equalTo(Creator.Role.Viewer));

        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_delete_existing_entity() {
        service.delete(new Creator.CreatorId(1L));
        verify(repository, times(1)).deleteById(any());
    }

    @Test
    public void returns_existing_entity() {
        var creator = createCreator();
        when(repository.getReferenceById(any())).thenReturn(creator);

        var returned = service.getByReference(new Creator.CreatorId(1L));

        assertNotNull(returned);
        assertThat(returned.getId(), equalTo(creator.getId()));

        verify(repository, times(1)).getReferenceById(any());
    }

    private Creator createCreator() {
        return Creator.builder()
                .id(new Creator.CreatorId(1L))
                .username("user")
                .role(Creator.Role.Editor)
                .build();
    }

}