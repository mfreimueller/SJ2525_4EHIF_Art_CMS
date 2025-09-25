package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.VisitHistory;
import com.mfreimueller.art.domain.Visitor;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class VisitorRepositoryTest {

    private @Autowired VisitorRepository visitorRepository;

    @Test
    void can_save_and_reread() {
        var visitHistory = VisitHistory.builder()
                .visitedOn(ZonedDateTime.now())
                .build();

        var visitor = Visitor.builder()
                .username("admin")
                .visitHistories(List.of(visitHistory))
                .build();

        var saved = visitorRepository.save(visitor);

        assertThat(saved).extracting(Visitor::getId).isNotNull();
        assertThat(saved).extracting(Visitor::getVisitHistories)
                .asInstanceOf(list(VisitHistory.class))
                .anySatisfy(vh -> assertThat(vh).extracting(VisitHistory::getId).isNotNull());
    }

}