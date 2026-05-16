package com.mfreimueller.art.presentation.web;

import com.mfreimueller.art.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/web/content")
public class ContentWebController {

    private final ContentService service;

    @GetMapping
    public String list(Model model) {
        var all = service.getPaged(null, 1000).getContent();
        model.addAttribute("contents", all);
        return "content/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, Authentication authentication) {
        var content = service.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Content not found: " + id));
        model.addAttribute("content", content);
        var canUpload = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EDITOR") || a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("canUpload", canUpload);
        return "content/detail";
    }

    @GetMapping("/pool")
    public String pool(Model model) {
        model.addAttribute("contents", service.getUnassignedContent());
        return "content/pool";
    }
}
