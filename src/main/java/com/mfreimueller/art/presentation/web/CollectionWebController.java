package com.mfreimueller.art.presentation.web;

import com.mfreimueller.art.commands.*;
import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.security.CurrentCreator;
import com.mfreimueller.art.service.CollectionService;
import com.mfreimueller.art.service.PointOfInterestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RequiredArgsConstructor
@Controller
@RequestMapping("/web/collections")
public class CollectionWebController {

    private final CollectionService service;
    private final PointOfInterestService poiService;
    private final CurrentCreator currentCreator;

    @GetMapping
    public String list(Model model) {
        var all = service.getCollections(Pageable.unpaged()).getContent();
        model.addAttribute("collections", all);
        return "collection/list";
    }

    @GetMapping("/{key}")
    public String detail(@PathVariable Long key, Model model) {
        var collection = service.getByReference(new Collection.CollectionId(key));
        model.addAttribute("collection", collection);
        model.addAttribute("allPois", poiService.getPointsOfInterest());
        model.addAttribute("allCollections", service.getCollections(Pageable.unpaged()).getContent()
                .stream().filter(c -> !c.getId().equals(collection.getId())).toList());
        return "collection/detail";
    }

    @GetMapping("/create")
    public String createForm() {
        return "collection/create";
    }

    @PostMapping("/create")
    public String create(
            @RequestParam(required = false) String titleEn,
            @RequestParam(required = false) String titleDe,
            Authentication auth
    ) {
        var title = new HashMap<String, String>();
        if (titleEn != null && !titleEn.isBlank()) title.put("en", titleEn);
        if (titleDe != null && !titleDe.isBlank()) title.put("de", titleDe);

        var cmd = CreateCollectionCommand.builder()
                .title(title)
                .creatorId(currentCreator.getId(auth))
                .build();

        var collection = service.create(cmd);
        return "redirect:/web/collections/" + collection.getId().id();
    }

    @GetMapping("/{key}/edit")
    public String editForm(@PathVariable Long key, Model model) {
        var collection = service.getByReference(new Collection.CollectionId(key));
        model.addAttribute("collection", collection);
        return "collection/edit";
    }

    @PostMapping("/{key}/edit")
    public String edit(
            @PathVariable Long key,
            @RequestParam(required = false) String titleEn,
            @RequestParam(required = false) String titleDe,
            Authentication auth
    ) {
        var title = new HashMap<String, String>();
        if (titleEn != null && !titleEn.isBlank()) title.put("en", titleEn);
        if (titleDe != null && !titleDe.isBlank()) title.put("de", titleDe);

        var cmd = UpdateCollectionCommand.builder()
                .title(title)
                .creatorId(currentCreator.getId(auth))
                .build();

        service.update(new Collection.CollectionId(key), cmd);
        return "redirect:/web/collections/" + key;
    }

    @GetMapping("/{key}/delete")
    public String deleteConfirm(@PathVariable Long key, Model model) {
        var collection = service.getByReference(new Collection.CollectionId(key));
        model.addAttribute("collection", collection);
        return "collection/confirm-delete";
    }

    @PostMapping("/{key}/delete")
    public String delete(@PathVariable Long key) {
        service.delete(new Collection.CollectionId(key));
        return "redirect:/web/collections";
    }

    @PostMapping("/{key}/pois/add")
    public String addPoi(@PathVariable Long key, @RequestParam Long poiId, Authentication auth) {
        var cmd = AddPointOfInterestCommand.builder()
                .poiId(new PointOfInterest.PointOfInterestId(poiId))
                .creatorId(currentCreator.getId(auth))
                .build();
        service.addPointOfInterest(new Collection.CollectionId(key), cmd);
        return "redirect:/web/collections/" + key;
    }

    @PostMapping("/{key}/pois/{poiKey}/remove")
    public String removePoi(@PathVariable Long key, @PathVariable Long poiKey, Authentication auth) {
        var cmd = RemovePointOfInterestCommand.builder()
                .poiId(new PointOfInterest.PointOfInterestId(poiKey))
                .creatorId(currentCreator.getId(auth))
                .build();
        service.removePointOfInterest(new Collection.CollectionId(key), cmd);
        return "redirect:/web/collections/" + key;
    }

    @PostMapping("/{key}/subcollections/add")
    public String addSubcollection(@PathVariable Long key, @RequestParam Long subcollectionId, Authentication auth) {
        var cmd = AddSubcollectionCommand.builder()
                .subcollectionId(new Collection.CollectionId(subcollectionId))
                .creatorId(currentCreator.getId(auth))
                .build();
        service.addSubcollection(new Collection.CollectionId(key), cmd);
        return "redirect:/web/collections/" + key;
    }

    @PostMapping("/{key}/subcollections/{subKey}/remove")
    public String removeSubcollection(@PathVariable Long key, @PathVariable Long subKey, Authentication auth) {
        var cmd = RemoveSubcollectionCommand.builder()
                .collectionId(new Collection.CollectionId(subKey))
                .creatorId(currentCreator.getId(auth))
                .build();
        service.removeSubcollection(new Collection.CollectionId(key), cmd);
        return "redirect:/web/collections/" + key;
    }
}
