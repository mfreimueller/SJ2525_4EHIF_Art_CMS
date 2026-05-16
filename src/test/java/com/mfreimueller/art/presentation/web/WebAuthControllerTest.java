package com.mfreimueller.art.presentation.web;

import com.mfreimueller.art.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import static org.assertj.core.api.Assertions.assertThat;

class WebAuthControllerTest extends AbstractWebControllerTest {

    @Autowired
    private WebAuthController controller;

    @MockitoBean
    private EmailService emailService;

    @Test
    void login_returns_login_view() {
        String viewName = controller.login();
        assertThat(viewName).isEqualTo("login");
    }

    @Test
    void register_form_returns_register_view() {
        String viewName = controller.registerForm();
        assertThat(viewName).isEqualTo("register");
    }

    @Test
    void register_with_matching_passwords_creates_user_and_redirects() {
        Model model = new BindingAwareModelMap();
        String viewName = controller.register("newuser", "password", "password", model);

        assertThat(viewName).isEqualTo("redirect:/login?registered");
        assertThat(model.containsAttribute("error")).isFalse();
    }

    @Test
    void register_with_mismatched_passwords_shows_error() {
        Model model = new BindingAwareModelMap();
        String viewName = controller.register("newuser", "password1", "password2", model);

        assertThat(viewName).isEqualTo("register");
        assertThat(model.getAttribute("error")).isEqualTo("Passwords do not match.");
    }

    @Test
    void redirect_root_redirects_to_pois() {
        String viewName = controller.redirectRoot();
        assertThat(viewName).isEqualTo("redirect:/web/pois");
    }
}
