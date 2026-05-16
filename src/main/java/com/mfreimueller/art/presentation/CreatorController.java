package com.mfreimueller.art.presentation;

import com.mfreimueller.art.commands.CreateCreatorCommand;
import com.mfreimueller.art.commands.UpdateCreatorCommand;
import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.dto.CreatorDto;
import com.mfreimueller.art.presentation.assembler.CreatorModelAssembler;
import com.mfreimueller.art.service.CreatorService;
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
@RequestMapping("/api/creators")
public class CreatorController {
    private final CreatorService service;
    private final CreatorModelAssembler assembler;

    @PostMapping
    public ResponseEntity<EntityModel<CreatorDto>> createCreator(
            @Valid @RequestBody CreateCreatorCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var creator = service.create(cmd);
        log.trace("Created Creator with id: {}", creator.getId());

        var model = assembler.toModel(creator);
        var self = model.getRequiredLink("self");

        logExit(log);

        return ResponseEntity.created(self.toUri()).body(model);
    }

    @GetMapping("/{key}")
    public ResponseEntity<EntityModel<CreatorDto>> getCreator(@PathVariable Long key) {
        logEnter(log);

        var entity = service.getByReference(new Creator.CreatorId(key));
        var model = assembler.toModel(entity);

        logExit(log);
        return ResponseEntity.ok(model);
    }

    @GetMapping
    public ResponseEntity<SlicedModel<EntityModel<CreatorDto>>> getCreators(Pageable pageable) {
        logEnter(log);

        var creators = service.getCreators(pageable);
        var items = creators.map(assembler::toModel).stream().toList();

        var self = linkTo(methodOn(CreatorController.class).getCreators(pageable)).withSelfRel();
        var metadata = new SlicedModel.SliceMetadata(creators.getSize(), creators.getNumber());
        var model = SlicedModel.of(items, metadata, self);

        if (creators.hasPrevious()) {
            model.add(linkTo(methodOn(CreatorController.class).getCreators(creators.previousPageable())).withRel("prev"));
        }
        if (creators.hasNext()) {
            model.add(linkTo(methodOn(CreatorController.class).getCreators(creators.nextPageable())).withRel("next"));
        }

        log.info("Retrieved {} Creator entities (paging with slices)", creators.getSize());
        logExit(log);

        return ResponseEntity.ok(model);
    }

    @PutMapping("/{key}")
    public ResponseEntity<EntityModel<CreatorDto>> replaceCreator(
            @PathVariable Long key, @Valid @RequestBody UpdateCreatorCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var creator = service.update(new Creator.CreatorId(key), cmd);
        var model = assembler.toModel(creator);
        var self = model.getRequiredLink("self");

        log.trace("Replaced Creator with id: {}", creator.getId());
        logExit(log);

        return ResponseEntity.ok().location(self.toUri()).body(model);
    }

    @PatchMapping("/{key}")
    public ResponseEntity<EntityModel<CreatorDto>> patchCreator(
            @PathVariable Long key, @Valid @RequestBody UpdateCreatorCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var creator = service.update(new Creator.CreatorId(key), cmd);
        var model = assembler.toModel(creator);
        var self = model.getRequiredLink("self");

        log.trace("Patched Creator with id: {}", creator.getId());
        logExit(log);

        return ResponseEntity.ok().location(self.toUri()).body(model);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteCreator(@PathVariable Long key) {
        logEnter(log);
        log.trace("Deleting Creator with key: {}", key);

        service.delete(new Creator.CreatorId(key));
        logExit(log);

        return ResponseEntity.noContent().build();
    }
}
