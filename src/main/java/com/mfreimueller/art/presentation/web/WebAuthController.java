package com.mfreimueller.art.presentation.web;

import com.mfreimueller.art.commands.CreateCreatorCommand;
import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.service.CreatorService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class WebAuthController {

    private final CreatorService creatorService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model
    ) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "register";
        }

        var cmd = CreateCreatorCommand.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(Creator.Role.VIEWER)
                .build();

        creatorService.create(cmd);

        return "redirect:/login?registered";
    }

    @GetMapping("/web/")
    public String redirectRoot() {
        return "redirect:/web/pois";
    }
}
