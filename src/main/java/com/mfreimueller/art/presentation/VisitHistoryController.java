package com.mfreimueller.art.presentation;

import com.mfreimueller.art.commands.CreateVisitHistoryCommand;
import com.mfreimueller.art.domain.VisitHistory;
import com.mfreimueller.art.dto.VisitHistoryDto;
import com.mfreimueller.art.presentation.assembler.VisitHistoryModelAssembler;
import com.mfreimueller.art.service.VisitHistoryService;
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
@RequestMapping("/api/visit-histories")
public class VisitHistoryController {
    private final VisitHistoryService service;
    private final VisitHistoryModelAssembler assembler;

    @PostMapping
    public ResponseEntity<EntityModel<VisitHistoryDto>> createVisitHistory(
            @Valid @RequestBody CreateVisitHistoryCommand cmd, BindingResult bindingResult
    ) {
        logEnter(log);

        if (bindingResult.hasErrors()) {
            log.error("Binding result error caught");
            return ResponseEntity.badRequest().build();
        }

        var visitHistory = service.create(cmd);
        log.trace("Created VisitHistory with id: {}", visitHistory.getId());

        var model = assembler.toModel(visitHistory);
        var self = model.getRequiredLink("self");

        logExit(log);

        return ResponseEntity.created(self.toUri()).body(model);
    }

    @GetMapping("/{key}")
    public ResponseEntity<EntityModel<VisitHistoryDto>> getVisitHistory(@PathVariable Long key) {
        logEnter(log);

        var entity = service.getByReference(new VisitHistory.VisitHistoryId(key));
        var model = assembler.toModel(entity);

        logExit(log);
        return ResponseEntity.ok(model);
    }

    @GetMapping
    public ResponseEntity<SlicedModel<EntityModel<VisitHistoryDto>>> getVisitHistories(Pageable pageable) {
        logEnter(log);

        var histories = service.getVisitHistories(pageable);
        var items = histories.map(assembler::toModel).stream().toList();

        var self = linkTo(methodOn(VisitHistoryController.class).getVisitHistories(pageable)).withSelfRel();
        var metadata = new SlicedModel.SliceMetadata(histories.getSize(), histories.getNumber());
        var model = SlicedModel.of(items, metadata, self);

        if (histories.hasPrevious()) {
            model.add(linkTo(methodOn(VisitHistoryController.class).getVisitHistories(histories.previousPageable())).withRel("prev"));
        }
        if (histories.hasNext()) {
            model.add(linkTo(methodOn(VisitHistoryController.class).getVisitHistories(histories.nextPageable())).withRel("next"));
        }

        log.info("Retrieved {} VisitHistory entities (paging with slices)", histories.getSize());
        logExit(log);

        return ResponseEntity.ok(model);
    }
}
