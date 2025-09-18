package com.mfreimueller.springrader.persistence;

import com.mfreimueller.springrader.domain.Exam;
import com.mfreimueller.springrader.richtypes.Duration;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.ZonedDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ExamRepositoryTest {

    private @Autowired ExamRepository examRepository;

    @Test
    void can_save_and_reread() {
        var exam = Exam.builder()
                .date(ZonedDateTime.now())
                .duration(Duration.of(50))
                .build();

        var saved = examRepository.save(exam);

        assertThat(saved).extracting(Exam::getId).isNotNull();
    }

}