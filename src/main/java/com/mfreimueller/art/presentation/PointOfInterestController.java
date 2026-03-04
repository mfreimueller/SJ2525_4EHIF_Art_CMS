package com.mfreimueller.art.presentation;

import com.mfreimueller.art.commands.CreatePointOfInterestCommand;
import com.mfreimueller.art.commands.UpdatePointOfInterestCommand;
import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.dto.PointOfInterestDto;
import com.mfreimueller.art.mappers.PointOfInterestMapper;
import com.mfreimueller.art.presentation.assembler.PointOfInterestModelAssembler;
import com.mfreimueller.art.service.PointOfInterestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.SlicedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import static com.mfreimueller.art.util.LogHelper.logEnter;
import static com.mfreimueller.art.util.LogHelper.logExit;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@Slf4j

@RestController
@RequestMapping("/api/pois")
public class PointOfInterestController {
    private final PointOfInterestService service;
    private final PointOfInterestModelAssembler assembler;

    @PostMapping
    public ResponseEntity<EntityModel<PointOfInterestDto>> createPointOfInterest(
            @Valid
            @RequestBody CreatePointOfInterestCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var poi = service.create(cmd);
        log.trace("Created PointOfInterest with id: {}", poi.getId());

        var model = assembler.toModel(poi);
        var self = model.getRequiredLink("self");

        logExit(log);

        return ResponseEntity.created(self.toUri()).body(model);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deletePointOfInterest(@PathVariable Long key) {
        logEnter(log);
        log.trace("Deleting PointOfInterest with key: {}", key);

        ResponseEntity<Void> result = service.delete(new PointOfInterest.PointOfInterestId(key)) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();

        logExit(log);
        return result;
    }

    @GetMapping("/{key}")
    public ResponseEntity<EntityModel<PointOfInterestDto>> getPointOfInterest(@PathVariable Long key) {
        logEnter(log);
        log.trace("Getting PointOfInterest with key: {}", key);
        var result = service
                .getPointOfInterest(new PointOfInterest.PointOfInterestId(key))
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

        logExit(log);
        return result;
    }

    @GetMapping
    public ResponseEntity<SlicedModel<EntityModel<PointOfInterestDto>>> getPointsOfInterest(Pageable pageable) {
        logEnter(log);

        var pois = service.getPointsOfInterest(pageable);
        var items = pois.map(assembler::toModel).stream().toList();

        var self = linkTo(methodOn(PointOfInterestController.class).getPointsOfInterest(pageable)).withSelfRel();

        var metadata = new SlicedModel.SliceMetadata(
                pois.getSize(),
                pois.getNumber()
        );

        var model = SlicedModel.of(items, metadata, self);

        if (pois.hasPrevious()) {
            var prev = linkTo(methodOn(PointOfInterestController.class).getPointsOfInterest(pois.previousPageable()))
                    .withRel("prev");
            model.add(prev);
        }

        if (pois.hasNext()) {
            var next = linkTo(methodOn(PointOfInterestController.class).getPointsOfInterest(pois.nextPageable()))
                    .withRel("next");
            model.add(next);
        }

        log.info("Retrieved {} PointOfInterest entities (paging with slices)", pois.getSize());
        logExit(log);

        return ResponseEntity.ok(model);
    }

    @GetMapping("/search/{lang}/{search}")
    public ResponseEntity<CollectionModel<EntityModel<PointOfInterestDto>>> search(@PathVariable String lang, @PathVariable String search, Pageable pageable) {
        logEnter(log);

        var pois = service.search(lang, search, pageable);

        var items = pois.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        var self = linkTo(methodOn(PointOfInterestController.class).search(lang, search, pageable)).withSelfRel();

        var metadata = new SlicedModel.SliceMetadata(
                pois.getSize(),
                pois.getNumber()
        );

        var model = SlicedModel.of(items, metadata, self);

        if (pois.hasPrevious()) {
            model.add(linkTo(methodOn(PointOfInterestController.class).search(lang, search, pois.previousPageable()))
                    .withRel("prev"));
        }

        if (pois.hasNext()) {
            model.add(linkTo(methodOn(PointOfInterestController.class).search(lang, search, pois.nextPageable()))
                    .withRel("next"));
        }

        log.info("Retrieved {} PointOfInterest entities after search (paging with slices)", pois.getSize());
        logExit(log);

        return ResponseEntity.ok(model);
    }

    @PutMapping("/{key}")
    public ResponseEntity<EntityModel<PointOfInterestDto>> replacePointOfInterest(
            @PathVariable Long key, @Valid
            @RequestBody UpdatePointOfInterestCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);
        log.trace("Replacing PointOfInterest with key: {}", key);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var result = service.replace(new PointOfInterest.PointOfInterestId(key), cmd);
        if (result.isEmpty()) {
            log.info("PointOfInterest to replace not found");
            return ResponseEntity.notFound().build();
        }

        var model = assembler.toModel(result.get());
        var self = model.getRequiredLink("self");

        log.trace("Replaced PointOfInterest with id: {}", result.get().getId());
        logExit(log);

        return ResponseEntity.ok().location(self.toUri()).body(model);
    }

    @PatchMapping("/{key}")
    public ResponseEntity<EntityModel<PointOfInterestDto>> patchPointOfInterest(
            @PathVariable Long key, @Valid
            @RequestBody UpdatePointOfInterestCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);
        log.trace("Patching PointOfInterest with key: {}", key);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var result = service.update(new PointOfInterest.PointOfInterestId(key), cmd);
        if (result.isEmpty()) {
            log.info("PointOfInterest to patch not found");
            return ResponseEntity.notFound().build();
        }

        var model = assembler.toModel(result.get());
        var self = model.getRequiredLink("self");

        log.trace("Patched PointOfInterest with id: {}", result.get().getId());
        logExit(log);

        return ResponseEntity.ok().location(self.toUri()).body(model);
    }
}
