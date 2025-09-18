package com.mfreimueller.springrader.persistence;

import com.mfreimueller.springrader.domain.Course;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CourseRepositoryTest {

    private @Autowired CourseRepository courseRepository;

    @Test
    void can_save_and_reread() {
        var course = new Course();

        var saved = courseRepository.save(course);

        assertThat(saved).extracting(Course::getId).isNotNull();
    }

}