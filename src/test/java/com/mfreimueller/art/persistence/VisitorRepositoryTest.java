package com.mfreimueller.art.persistence;

import com.mfreimueller.art.domain.Visitor;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.ZonedDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class VisitorRepositoryTest {

    private @Autowired VisitorRepository visitorRepository;

    @Test
    void can_save_and_reread() {
        var exam = Visitor.builder()
                .username("admin")
                .firstVisitTS(ZonedDateTime.now())
                .build();

        var saved = visitorRepository.save(exam);

        assertThat(saved).extracting(Visitor::getId).isNotNull();
    }

}