package com.mfreimueller.art;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
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
        final int containerPort = 5432;

        PortBinding portBinding = new PortBinding(Ports.Binding.bindPort(15432), new ExposedPort(containerPort));

        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.6-alpine"))
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName("pg.sj2526.4ehif");
                    cmd.withHostConfig(new HostConfig().withPortBindings(portBinding));
                })
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
