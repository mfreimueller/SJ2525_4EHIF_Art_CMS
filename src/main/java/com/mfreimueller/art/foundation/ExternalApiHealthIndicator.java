package com.mfreimueller.art.foundation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalApiHealthIndicator implements HealthIndicator {

    private final RestClient.Builder restClientBuilder;

    @Override
    public Health health() {
        try {
            var client = restClientBuilder
                    .baseUrl("https://dummyjson.com")
                    .build();
            var response = client.get()
                    .uri("/comments?limit=1")
                    .retrieve()
                    .toBodilessEntity();

            if (response.getStatusCode().is2xxSuccessful()) {
                return Health.up()
                        .withDetail("service", "dummyjson.com")
                        .withDetail("statusCode", response.getStatusCode().value())
                        .build();
            } else {
                return Health.down()
                        .withDetail("service", "dummyjson.com")
                        .withDetail("statusCode", response.getStatusCode().value())
                        .build();
            }
        } catch (Exception e) {
            log.warn("External API health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("service", "dummyjson.com")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
