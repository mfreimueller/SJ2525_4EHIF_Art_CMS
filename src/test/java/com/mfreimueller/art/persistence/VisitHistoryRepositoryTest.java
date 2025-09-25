package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.VisitHistory;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.ZonedDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class VisitHistoryRepositoryTest {

    private @Autowired VisitHistoryRepository visitHistoryRepository;

    @Test
    void can_save_and_reread() {
        var visitHistory = VisitHistory.builder()
                .visitedOn(ZonedDateTime.now())
                .build();

        var saved = visitHistoryRepository.save(visitHistory);

        assertThat(saved).extracting(VisitHistory::getId).isNotNull();
    }

}