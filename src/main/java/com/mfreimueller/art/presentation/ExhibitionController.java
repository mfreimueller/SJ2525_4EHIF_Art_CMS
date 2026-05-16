package com.mfreimueller.art.presentation;

import com.mfreimueller.art.commands.*;
import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.dto.ExhibitionDto;
import com.mfreimueller.art.presentation.assembler.ExhibitionModelAssembler;
import com.mfreimueller.art.service.ExhibitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.SlicedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.mfreimueller.art.util.LogHelper.logEnter;
import static com.mfreimueller.art.util.LogHelper.logExit;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@Slf4j

@RestController
@RequestMapping("/api/exhibitions")
public class ExhibitionController {
    private final ExhibitionService service;
    private final ExhibitionModelAssembler assembler;

    @PostMapping
    public ResponseEntity<EntityModel<ExhibitionDto>> createExhibition(
            @Valid @RequestBody CreateExhibitionCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var exhibition = service.create(cmd);
        log.trace("Created Exhibition with id: {}", exhibition.getId());

        var model = assembler.toModel(exhibition);
        var self = model.getRequiredLink("self");

        logExit(log);

        return ResponseEntity.created(self.toUri()).body(model);
    }

    @GetMapping("/{key}")
    public ResponseEntity<EntityModel<ExhibitionDto>> getExhibition(@PathVariable Long key) {
        logEnter(log);

        var entity = service.getByReference(new Collection.CollectionId(key));
        var model = assembler.toModel(entity);

        logExit(log);
        return ResponseEntity.ok(model);
    }

    @GetMapping
    public ResponseEntity<SlicedModel<EntityModel<ExhibitionDto>>> getExhibitions(Pageable pageable) {
        logEnter(log);

        var exhibitions = service.getExhibitions(pageable);
        var items = exhibitions.map(assembler::toModel).stream().toList();

        var self = linkTo(methodOn(ExhibitionController.class).getExhibitions(pageable)).withSelfRel();
        var metadata = new SlicedModel.SliceMetadata(exhibitions.getSize(), exhibitions.getNumber());
        var model = SlicedModel.of(items, metadata, self);

        if (exhibitions.hasPrevious()) {
            model.add(linkTo(methodOn(ExhibitionController.class).getExhibitions(exhibitions.previousPageable())).withRel("prev"));
        }
        if (exhibitions.hasNext()) {
            model.add(linkTo(methodOn(ExhibitionController.class).getExhibitions(exhibitions.nextPageable())).withRel("next"));
        }

        log.info("Retrieved {} Exhibition entities (paging with slices)", exhibitions.getSize());
        logExit(log);

        return ResponseEntity.ok(model);
    }

    @PutMapping("/{key}")
    public ResponseEntity<EntityModel<ExhibitionDto>> replaceExhibition(
            @PathVariable Long key, @Valid @RequestBody UpdateExhibitionCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var exhibition = service.update(new Collection.CollectionId(key), cmd);
        var model = assembler.toModel(exhibition);
        var self = model.getRequiredLink("self");

        log.trace("Replaced Exhibition with id: {}", exhibition.getId());
        logExit(log);

        return ResponseEntity.ok().location(self.toUri()).body(model);
    }

    @PatchMapping("/{key}")
    public ResponseEntity<EntityModel<ExhibitionDto>> patchExhibition(
            @PathVariable Long key, @Valid @RequestBody UpdateExhibitionCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var exhibition = service.update(new Collection.CollectionId(key), cmd);
        var model = assembler.toModel(exhibition);
        var self = model.getRequiredLink("self");

        log.trace("Patched Exhibition with id: {}", exhibition.getId());
        logExit(log);

        return ResponseEntity.ok().location(self.toUri()).body(model);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteExhibition(@PathVariable Long key) {
        logEnter(log);
        log.trace("Deleting Exhibition with key: {}", key);

        service.delete(new Collection.CollectionId(key));
        logExit(log);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{key}/pois")
    public ResponseEntity<EntityModel<ExhibitionDto>> addPointOfInterest(
            @PathVariable Long key, @Valid @RequestBody AddPointOfInterestCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var collection = service.addPointOfInterest(new Collection.CollectionId(key), cmd);
        var model = assembler.toModel((com.mfreimueller.art.domain.Exhibition) collection);
        var self = model.getRequiredLink("self");

        log.trace("Added POI {} to Exhibition {}", cmd.poiId(), key);
        logExit(log);

        return ResponseEntity.ok().location(self.toUri()).body(model);
    }

    @DeleteMapping("/{key}/pois/{poiKey}")
    public ResponseEntity<Void> removePointOfInterest(
            @PathVariable Long key, @PathVariable Long poiKey,
            @RequestParam Long creatorId
    ) {
        logEnter(log);
        log.trace("Removing POI {} from Exhibition {}", poiKey, key);

        var cmd = RemovePointOfInterestCommand.builder()
                .poiId(new PointOfInterest.PointOfInterestId(poiKey))
                .creatorId(new com.mfreimueller.art.domain.Creator.CreatorId(creatorId))
                .build();

        service.removePointOfInterest(new Collection.CollectionId(key), cmd);
        logExit(log);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{key}/subcollections")
    public ResponseEntity<EntityModel<ExhibitionDto>> addSubcollection(
            @PathVariable Long key, @Valid @RequestBody AddSubcollectionCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var collection = service.addSubcollection(new Collection.CollectionId(key), cmd);
        var model = assembler.toModel((com.mfreimueller.art.domain.Exhibition) collection);
        var self = model.getRequiredLink("self");

        log.trace("Added subcollection {} to Exhibition {}", cmd.subcollectionId(), key);
        logExit(log);

        return ResponseEntity.ok().location(self.toUri()).body(model);
    }

    @DeleteMapping("/{key}/subcollections/{subKey}")
    public ResponseEntity<Void> removeSubcollection(
            @PathVariable Long key, @PathVariable Long subKey,
            @RequestParam Long creatorId
    ) {
        logEnter(log);
        log.trace("Removing subcollection {} from Exhibition {}", subKey, key);

        var cmd = RemoveSubcollectionCommand.builder()
                .collectionId(new Collection.CollectionId(subKey))
                .creatorId(new com.mfreimueller.art.domain.Creator.CreatorId(creatorId))
                .build();

        service.removeSubcollection(new Collection.CollectionId(key), cmd);
        logExit(log);

        return ResponseEntity.noContent().build();
    }
}
