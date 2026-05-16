package com.mfreimueller.art.presentation.web;

import com.mfreimueller.art.commands.CreateCreatorCommand;
import com.mfreimueller.art.commands.UpdateCreatorCommand;
import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.service.CreatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/web/creators")
public class CreatorWebController {

    private final CreatorService service;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String list(Model model) {
        var creators = service.getCreators(Pageable.unpaged()).getContent();
        model.addAttribute("creators", creators);
        return "creator/list";
    }

    @GetMapping("/{key}")
    public String detail(@PathVariable Long key, Model model) {
        var creator = service.getByReference(new Creator.CreatorId(key));
        model.addAttribute("creator", creator);
        return "creator/detail";
    }

    @GetMapping("/create")
    public String createForm() {
        return "creator/create";
    }

    @PostMapping("/create")
    public String create(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam Creator.Role role
    ) {
        var cmd = CreateCreatorCommand.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
        var creator = service.create(cmd);
        return "redirect:/web/creators/" + creator.getId().id();
    }

    @GetMapping("/{key}/edit")
    public String editForm(@PathVariable Long key, Model model) {
        var creator = service.getByReference(new Creator.CreatorId(key));
        model.addAttribute("creator", creator);
        return "creator/edit";
    }

    @PostMapping("/{key}/edit")
    public String edit(
            @PathVariable Long key,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam Creator.Role role
    ) {
        var cmd = UpdateCreatorCommand.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
        service.update(new Creator.CreatorId(key), cmd);
        return "redirect:/web/creators/" + key;
    }

    @GetMapping("/{key}/delete")
    public String deleteConfirm(@PathVariable Long key, Model model) {
        var creator = service.getByReference(new Creator.CreatorId(key));
        model.addAttribute("creator", creator);
        return "creator/confirm-delete";
    }

    @PostMapping("/{key}/delete")
    public String delete(@PathVariable Long key) {
        service.delete(new Creator.CreatorId(key));
        return "redirect:/web/creators";
    }
}
