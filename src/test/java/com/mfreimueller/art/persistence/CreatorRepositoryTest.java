package com.mfreimueller.art.persistence;

import com.mfreimueller.art.TestcontainersConfiguration;
import com.mfreimueller.art.domain.Creator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Import(TestcontainersConfiguration.class)
class CreatorRepositoryTest {

    private @Autowired CreatorRepository creatorRepository;

    @Test
    void can_save_and_reread() {
        var exam = Creator.builder()
                .username("admin")
                .role(Creator.Role.ADMIN)
                .build();

        var saved = creatorRepository.save(exam);

        assertThat(saved).extracting(Creator::getId).isNotNull();
    }

}