package com.mfreimueller.art.presentation.web;

import com.mfreimueller.art.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import(TestcontainersConfiguration.class)
public abstract class AbstractWebControllerTest {

    protected final static String ADMIN_VIEW = "redirect:/web/pois";

    protected static Authentication adminAuth() {
        return new UsernamePasswordAuthenticationToken(
                "admin", "admin",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    protected static Authentication editorAuth() {
        return new UsernamePasswordAuthenticationToken(
                "editor", "editor",
                List.of(new SimpleGrantedAuthority("ROLE_EDITOR")));
    }

    @BeforeEach
    void setUp() {
    }
}
