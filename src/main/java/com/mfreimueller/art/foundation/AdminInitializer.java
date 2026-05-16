package com.mfreimueller.art.foundation;

import com.mfreimueller.art.commands.CreateCreatorCommand;
import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.persistence.CreatorRepository;
import com.mfreimueller.art.service.CreatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class AdminInitializer implements CommandLineRunner {

    private final CreatorRepository creatorRepository;
    private final PasswordEncoder passwordEncoder;
    private final CreatorService creatorService;

    @Override
    public void run(String... args) {
        if (creatorRepository.findByUsername("admin").isEmpty()) {
            var cmd = CreateCreatorCommand.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .role(Creator.Role.ADMIN)
                    .build();
            creatorService.create(cmd);
            log.info("Created admin user (username=admin, password=admin, role=ADMIN)");
        } else {
            log.debug("Admin user already exists");
        }
    }
}
