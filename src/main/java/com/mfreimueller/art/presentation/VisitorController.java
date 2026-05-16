package com.mfreimueller.art.presentation;

import com.mfreimueller.art.commands.CreateVisitorCommand;
import com.mfreimueller.art.commands.UpdateVisitorCommand;
import com.mfreimueller.art.domain.Visitor;
import com.mfreimueller.art.dto.VisitorDto;
import com.mfreimueller.art.presentation.assembler.VisitorModelAssembler;
import com.mfreimueller.art.service.VisitorService;
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
@RequestMapping("/api/visitors")
public class VisitorController {
    private final VisitorService service;
    private final VisitorModelAssembler assembler;

    @PostMapping
    public ResponseEntity<EntityModel<VisitorDto>> createVisitor(
            @Valid @RequestBody CreateVisitorCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var visitor = service.create(cmd);
        log.trace("Created Visitor with id: {}", visitor.getId());

        var model = assembler.toModel(visitor);
        var self = model.getRequiredLink("self");

        logExit(log);

        return ResponseEntity.created(self.toUri()).body(model);
    }

    @GetMapping("/{key}")
    public ResponseEntity<EntityModel<VisitorDto>> getVisitor(@PathVariable Long key) {
        logEnter(log);

        var entity = service.getByReference(new Visitor.VisitorId(key));
        var model = assembler.toModel(entity);

        logExit(log);
        return ResponseEntity.ok(model);
    }

    @GetMapping
    public ResponseEntity<SlicedModel<EntityModel<VisitorDto>>> getVisitors(Pageable pageable) {
        logEnter(log);

        var visitors = service.getVisitors(pageable);
        var items = visitors.map(assembler::toModel).stream().toList();

        var self = linkTo(methodOn(VisitorController.class).getVisitors(pageable)).withSelfRel();
        var metadata = new SlicedModel.SliceMetadata(visitors.getSize(), visitors.getNumber());
        var model = SlicedModel.of(items, metadata, self);

        if (visitors.hasPrevious()) {
            model.add(linkTo(methodOn(VisitorController.class).getVisitors(visitors.previousPageable())).withRel("prev"));
        }
        if (visitors.hasNext()) {
            model.add(linkTo(methodOn(VisitorController.class).getVisitors(visitors.nextPageable())).withRel("next"));
        }

        log.info("Retrieved {} Visitor entities (paging with slices)", visitors.getSize());
        logExit(log);

        return ResponseEntity.ok(model);
    }

    @PutMapping("/{key}")
    public ResponseEntity<EntityModel<VisitorDto>> replaceVisitor(
            @PathVariable Long key, @Valid @RequestBody UpdateVisitorCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var visitor = service.update(new Visitor.VisitorId(key), cmd);
        var model = assembler.toModel(visitor);
        var self = model.getRequiredLink("self");

        log.trace("Replaced Visitor with id: {}", visitor.getId());
        logExit(log);

        return ResponseEntity.ok().location(self.toUri()).body(model);
    }

    @PatchMapping("/{key}")
    public ResponseEntity<EntityModel<VisitorDto>> patchVisitor(
            @PathVariable Long key, @Valid @RequestBody UpdateVisitorCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var visitor = service.update(new Visitor.VisitorId(key), cmd);
        var model = assembler.toModel(visitor);
        var self = model.getRequiredLink("self");

        log.trace("Patched Visitor with id: {}", visitor.getId());
        logExit(log);

        return ResponseEntity.ok().location(self.toUri()).body(model);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteVisitor(@PathVariable Long key) {
        logEnter(log);
        log.trace("Deleting Visitor with key: {}", key);

        service.delete(new Visitor.VisitorId(key));
        logExit(log);

        return ResponseEntity.noContent().build();
    }
}
