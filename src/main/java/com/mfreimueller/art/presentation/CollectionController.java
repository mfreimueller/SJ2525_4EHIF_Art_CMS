package com.mfreimueller.art.presentation;

import com.mfreimueller.art.commands.*;
import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.dto.CollectionDto;
import com.mfreimueller.art.presentation.assembler.CollectionModelAssembler;
import com.mfreimueller.art.service.CollectionService;
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
@RequestMapping("/api/collections")
public class CollectionController {
    private final CollectionService service;
    private final CollectionModelAssembler assembler;

    @PostMapping
    public ResponseEntity<EntityModel<CollectionDto>> createCollection(
            @Valid @RequestBody CreateCollectionCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var collection = service.create(cmd);
        log.trace("Created Collection with id: {}", collection.getId());

        var model = assembler.toModel(collection);
        var self = model.getRequiredLink("self");

        logExit(log);

        return ResponseEntity.created(self.toUri()).body(model);
    }

    @GetMapping("/{key}")
    public ResponseEntity<EntityModel<CollectionDto>> getCollection(@PathVariable Long key) {
        logEnter(log);

        var entity = service.getByReference(new Collection.CollectionId(key));
        var model = assembler.toModel(entity);

        logExit(log);
        return ResponseEntity.ok(model);
    }

    @GetMapping
    public ResponseEntity<SlicedModel<EntityModel<CollectionDto>>> getCollections(Pageable pageable) {
        logEnter(log);

        var collections = service.getCollections(pageable);
        var items = collections.map(assembler::toModel).stream().toList();

        var self = linkTo(methodOn(CollectionController.class).getCollections(pageable)).withSelfRel();
        var metadata = new SlicedModel.SliceMetadata(collections.getSize(), collections.getNumber());
        var model = SlicedModel.of(items, metadata, self);

        if (collections.hasPrevious()) {
            model.add(linkTo(methodOn(CollectionController.class).getCollections(collections.previousPageable())).withRel("prev"));
        }
        if (collections.hasNext()) {
            model.add(linkTo(methodOn(CollectionController.class).getCollections(collections.nextPageable())).withRel("next"));
        }

        log.info("Retrieved {} Collection entities (paging with slices)", collections.getSize());
        logExit(log);

        return ResponseEntity.ok(model);
    }

    @PutMapping("/{key}")
    public ResponseEntity<EntityModel<CollectionDto>> replaceCollection(
            @PathVariable Long key, @Valid @RequestBody UpdateCollectionCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var collection = service.update(new Collection.CollectionId(key), cmd);
        var model = assembler.toModel(collection);
        var self = model.getRequiredLink("self");

        log.trace("Replaced Collection with id: {}", collection.getId());
        logExit(log);

        return ResponseEntity.ok().location(self.toUri()).body(model);
    }

    @PatchMapping("/{key}")
    public ResponseEntity<EntityModel<CollectionDto>> patchCollection(
            @PathVariable Long key, @Valid @RequestBody UpdateCollectionCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var collection = service.update(new Collection.CollectionId(key), cmd);
        var model = assembler.toModel(collection);
        var self = model.getRequiredLink("self");

        log.trace("Patched Collection with id: {}", collection.getId());
        logExit(log);

        return ResponseEntity.ok().location(self.toUri()).body(model);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteCollection(@PathVariable Long key) {
        logEnter(log);
        log.trace("Deleting Collection with key: {}", key);

        service.delete(new Collection.CollectionId(key));
        logExit(log);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{key}/pois")
    public ResponseEntity<EntityModel<CollectionDto>> addPointOfInterest(
            @PathVariable Long key, @Valid @RequestBody AddPointOfInterestCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var collection = service.addPointOfInterest(new Collection.CollectionId(key), cmd);
        var model = assembler.toModel(collection);
        var self = model.getRequiredLink("self");

        log.trace("Added POI {} to Collection {}", cmd.poiId(), key);
        logExit(log);

        return ResponseEntity.ok().location(self.toUri()).body(model);
    }

    @DeleteMapping("/{key}/pois/{poiKey}")
    public ResponseEntity<Void> removePointOfInterest(
            @PathVariable Long key, @PathVariable Long poiKey,
            @RequestParam Long creatorId
    ) {
        logEnter(log);
        log.trace("Removing POI {} from Collection {}", poiKey, key);

        var cmd = RemovePointOfInterestCommand.builder()
                .poiId(new com.mfreimueller.art.domain.PointOfInterest.PointOfInterestId(poiKey))
                .creatorId(new com.mfreimueller.art.domain.Creator.CreatorId(creatorId))
                .build();

        service.removePointOfInterest(new Collection.CollectionId(key), cmd);
        logExit(log);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{key}/subcollections")
    public ResponseEntity<EntityModel<CollectionDto>> addSubcollection(
            @PathVariable Long key, @Valid @RequestBody AddSubcollectionCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var collection = service.addSubcollection(new Collection.CollectionId(key), cmd);
        var model = assembler.toModel(collection);
        var self = model.getRequiredLink("self");

        log.trace("Added subcollection {} to Collection {}", cmd.subcollectionId(), key);
        logExit(log);

        return ResponseEntity.ok().location(self.toUri()).body(model);
    }

    @DeleteMapping("/{key}/subcollections/{subKey}")
    public ResponseEntity<Void> removeSubcollection(
            @PathVariable Long key, @PathVariable Long subKey,
            @RequestParam Long creatorId
    ) {
        logEnter(log);
        log.trace("Removing subcollection {} from Collection {}", subKey, key);

        var cmd = RemoveSubcollectionCommand.builder()
                .collectionId(new Collection.CollectionId(subKey))
                .creatorId(new com.mfreimueller.art.domain.Creator.CreatorId(creatorId))
                .build();

        service.removeSubcollection(new Collection.CollectionId(key), cmd);
        logExit(log);

        return ResponseEntity.noContent().build();
    }
}
