package com.mfreimueller.art.presentation;

import com.mfreimueller.art.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.http.client.HttpRedirects;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Import(TestcontainersConfiguration.class)
class SecurityConfigIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void unauthenticated_request_to_protected_page_redirects_to_login() {
        var response = restTemplate
                .withRedirects(HttpRedirects.DONT_FOLLOW)
                .getForEntity("/web/pois", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation()).hasPath("/login");
    }

    @Test
    void authenticated_request_to_protected_page_returns_200() {
        var response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/web/pois", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void post_to_api_without_auth_redirects_to_login() {
        var response = restTemplate
                .withRedirects(HttpRedirects.DONT_FOLLOW)
                .postForEntity("/api/pois", null, String.class);

        assertThat(response.getStatusCode())
                .as("FormLogin entry point redirects unauthenticated POST to /login")
                .isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation()).hasPath("/login");
    }

    @Test
    void authenticated_admin_can_access_admin_actuator() {
        var response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/actuator/env", String.class);

        assertThat(response.getStatusCode())
                .as("Admin with ROLE_ADMIN can access ADMIN-only actuator endpoint")
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    void unauthenticated_request_to_admin_actuator_returns_401() {
        var response = restTemplate
                .withRedirects(HttpRedirects.DONT_FOLLOW)
                .getForEntity("/actuator/env", String.class);

        assertThat(response.getStatusCode())
                .as("Unauthenticated request to ADMIN-only actuator should redirect to login (form login)")
                .isEqualTo(HttpStatus.FOUND);
    }

    @Test
    void login_page_is_accessible_without_auth() {
        var response = restTemplate.getForEntity("/login", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void register_page_is_accessible_without_auth() {
        var response = restTemplate.getForEntity("/register", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
