package com.mfreimueller.art.foundation;

import com.mfreimueller.art.TestcontainersConfiguration;
import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.persistence.CreatorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class AdminInitializerTest {

    @Autowired
    private CreatorRepository creatorRepository;

    @Test
    void admin_user_exists_after_context_start() {
        var admin = creatorRepository.findByUsername("admin");

        assertThat(admin).isPresent();
        assertThat(admin.get().getUsername()).isEqualTo("admin");
        assertThat(admin.get().getRole()).isEqualTo(Creator.Role.ADMIN);
    }
}
