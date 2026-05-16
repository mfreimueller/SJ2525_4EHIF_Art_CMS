package com.mfreimueller.art;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class TestMain {
    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> testMainPostgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.6-alpine"))
                .withDatabaseName("pg2526_4ehif")
                .withUsername("owner")
                .withPassword("pwd")
                .withReuse(true);
    }

    public static void main(String[] args) {
        SpringApplication.from(ArtCmsApplication::main)
                .with(TestMain.class)
                .run(args);
    }
}
