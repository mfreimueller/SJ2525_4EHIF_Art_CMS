package com.mfreimueller.art.presentation.web;

import com.mfreimueller.art.commands.CreatePointOfInterestCommand;
import com.mfreimueller.art.commands.UpdatePointOfInterestCommand;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.security.CurrentCreator;
import com.mfreimueller.art.service.PointOfInterestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/web/pois")
public class PointOfInterestWebController {

    private final PointOfInterestService service;
    private final CurrentCreator currentCreator;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pois", service.getPointsOfInterest());
        return "poi/list";
    }

    @GetMapping("/{key}")
    public String detail(@PathVariable Long key, Model model) {
        var poi = service.getPointOfInterest(new PointOfInterest.PointOfInterestId(key))
                .orElseThrow(() -> new IllegalArgumentException("POI not found: " + key));
        model.addAttribute("poi", poi);
        return "poi/detail";
    }

    @GetMapping("/create")
    public String createForm() {
        return "poi/create";
    }

    @PostMapping("/create")
    public String create(
            @RequestParam(required = false) String titleEn,
            @RequestParam(required = false) String titleDe,
            @RequestParam(required = false) String descEn,
            @RequestParam(required = false) String descDe,
            Authentication auth
    ) {
        var title = new HashMap<String, String>();
        if (titleEn != null && !titleEn.isBlank()) title.put("en", titleEn);
        if (titleDe != null && !titleDe.isBlank()) title.put("de", titleDe);

        var description = new HashMap<String, String>();
        if (descEn != null && !descEn.isBlank()) description.put("en", descEn);
        if (descDe != null && !descDe.isBlank()) description.put("de", descDe);

        var cmd = CreatePointOfInterestCommand.builder()
                .title(title)
                .description(description)
                .content(List.of())
                .creatorId(currentCreator.getId(auth))
                .build();

        var poi = service.create(cmd);
        return "redirect:/web/pois/" + poi.getId().id();
    }

    @GetMapping("/{key}/edit")
    public String editForm(@PathVariable Long key, Model model) {
        var poi = service.getPointOfInterest(new PointOfInterest.PointOfInterestId(key))
                .orElseThrow(() -> new IllegalArgumentException("POI not found: " + key));
        model.addAttribute("poi", poi);
        return "poi/edit";
    }

    @PostMapping("/{key}/edit")
    public String edit(
            @PathVariable Long key,
            @RequestParam(required = false) String titleEn,
            @RequestParam(required = false) String titleDe,
            @RequestParam(required = false) String descEn,
            @RequestParam(required = false) String descDe,
            Authentication auth
    ) {
        var title = new HashMap<String, String>();
        if (titleEn != null && !titleEn.isBlank()) title.put("en", titleEn);
        if (titleDe != null && !titleDe.isBlank()) title.put("de", titleDe);

        var description = new HashMap<String, String>();
        if (descEn != null && !descEn.isBlank()) description.put("en", descEn);
        if (descDe != null && !descDe.isBlank()) description.put("de", descDe);

        var cmd = UpdatePointOfInterestCommand.builder()
                .title(title)
                .description(description)
                .content(List.of())
                .creatorId(currentCreator.getId(auth))
                .build();

        service.replace(new PointOfInterest.PointOfInterestId(key), cmd);
        return "redirect:/web/pois/" + key;
    }

    @GetMapping("/{key}/delete")
    public String deleteConfirm(@PathVariable Long key, Model model) {
        var poi = service.getPointOfInterest(new PointOfInterest.PointOfInterestId(key))
                .orElseThrow(() -> new IllegalArgumentException("POI not found: " + key));
        model.addAttribute("poi", poi);
        return "poi/confirm-delete";
    }

    @PostMapping("/{key}/delete")
    public String delete(@PathVariable Long key) {
        service.delete(new PointOfInterest.PointOfInterestId(key));
        return "redirect:/web/pois";
    }

    @GetMapping("/pool")
    public String pool(Model model) {
        model.addAttribute("pois", service.getUnassignedPointsOfInterest());
        return "poi/pool";
    }
}
