package com.mfreimueller.art.service;

import com.github.javafaker.Faker;
import com.mfreimueller.art.commands.CreateVisitorCommand;
import com.mfreimueller.art.commands.UpdateVisitorCommand;
import com.mfreimueller.art.domain.Visitor;
import com.mfreimueller.art.persistence.VisitorRepository;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class VisitorServiceTest {

    private @InjectMocks VisitorService service;
    private @Mock VisitorRepository repository;

    private final Faker faker = new Faker();

    @BeforeEach
    public void setup() {
        assumeThat(service).isNotNull();
        assumeThat(repository).isNotNull();
    }

    @Test
    public void can_create_with_valid_data() {
        var cmd = CreateVisitorCommand.builder()
                .username(faker.name().username())
                .emailAddress(validEmail())
                .build();

        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var visitor = service.create(cmd);

        assertNotNull(visitor);
        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_update_existing_entity() {
        var visitor = visitor();

        final String username = faker.name().username();
        final String email = validEmail();

        var cmd = UpdateVisitorCommand.builder()
                .username(username)
                .email(email)
                .build();

        when(repository.getReferenceById(any())).thenReturn(visitor);
        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var returned = service.update(visitor.getId(), cmd);

        assertNotNull(returned);
        assertThat(returned.getUsername(), equalTo(username));
        assertThat(returned.getEmailAddress(), equalTo(email));

        verify(repository, times(1)).save(any());
    }

    @Test
    public void can_delete_existing_entity() {
        service.delete(new Visitor.VisitorId(1L));
        verify(repository, times(1)).deleteById(any());
    }

    @Test
    public void returns_existing_entity() {
        var visitor = visitor();
        when(repository.getReferenceById(any())).thenReturn(visitor);

        var returned = service.getByReference(new Visitor.VisitorId(1L));

        assertNotNull(returned);
        assertThat(returned.getId(), equalTo(visitor.getId()));

        verify(repository, times(1)).getReferenceById(any());
    }

}