package com.mfreimueller.art.service;

import com.github.javafaker.Faker;
import com.mfreimueller.art.commands.CreateVisitHistoryCommand;
import com.mfreimueller.art.domain.VisitHistory;
import com.mfreimueller.art.domain.Visitor;
import com.mfreimueller.art.persistence.VisitHistoryRepository;
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

import java.util.List;

import static com.mfreimueller.art.service.ServiceFixtures.*;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class VisitHistoryServiceTest {

    private @InjectMocks VisitHistoryService service;
    private @Mock VisitHistoryRepository repository;
    private @Mock VisitorService visitorService;

    @BeforeEach
    public void setup() {
        assumeThat(service).isNotNull();
        assumeThat(repository).isNotNull();
        assumeThat(visitorService).isNotNull();
    }

    @Test
    public void can_create_with_valid_data() {
        var visitor = createVisitor();

        var cmd = CreateVisitHistoryCommand.builder()
                .visitedOn(createPastDateTime())
                .duration(Duration.of(60))
                .pointsOfInterest(List.of())
                .visitorId(visitor.getId())
                .build();

        when(visitorService.getByReference(any())).thenReturn(visitor);
        when(repository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());

        var visitHistory = service.create(cmd);

        assertNotNull(visitHistory);
        assertThat(visitHistory.getVisitor(), notNullValue());

        verify(repository, times(1)).save(any());
    }

    @Test
    public void returns_existing_entity() {
        var visitHistory = VisitHistory.builder()
                        .id(new VisitHistory.VisitHistoryId(1L))
                        .build();

        when(repository.getReferenceById(any())).thenReturn(visitHistory);

        var returned = service.getByReference(new VisitHistory.VisitHistoryId(1L));

        assertNotNull(returned);
        assertThat(returned.getId(), equalTo(visitHistory.getId()));

        verify(repository, times(1)).getReferenceById(any());
    }

}