package com.mfreimueller.art.presentation;

import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.dto.ContentDto;
import com.mfreimueller.art.presentation.assembler.ContentModelAssembler;
import com.mfreimueller.art.service.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.SlicedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.mfreimueller.art.util.LogHelper.logEnter;
import static com.mfreimueller.art.util.LogHelper.logExit;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@Slf4j

@RestController
@RequestMapping("/api/content")
public class ContentController {
    private final ContentService service;

    private final ContentModelAssembler assembler;

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ContentDto>> getContent(@PathVariable("id") Long id) {
        logEnter(log);

        var content = Optional.ofNullable(service.getByReference(id));
        var result = content
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

        logExit(log);
        return result;
    }

    @GetMapping
    public ResponseEntity<SlicedModel<EntityModel<ContentDto>>> getContents(
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "20") int pageSize
            ) {
        logEnter(log);

        var content = service.getPaged(lastId, pageSize);
        var items = content.map(assembler::toModel).stream().toList();

        var self = linkTo(methodOn(ContentController.class).getContents(lastId, pageSize)).withSelfRel();

        var metadata = new SlicedModel.SliceMetadata(
                content.getSize(),
                content.getNumber()
        );

        var model = SlicedModel.of(items, metadata, self);

        if (content.hasPrevious()) {
            var page = content.previousPageable();
            var prevLastId = content.getContent().getFirst().getId() - page.getPageSize();
            var prev = linkTo(methodOn(ContentController.class).getContents(prevLastId, page.getPageSize()))
                    .withRel("prev");
            model.add(prev);
        }

        if (content.hasNext()) {
            var next = linkTo(methodOn(ContentController.class).getContents(
                    content.getContent().getLast().getId(), pageSize))
                    .withRel("next");
            model.add(next);
        }

        log.info("Retrieved {} Content entities (keyset paging with slices)", content.getSize());
        logExit(log);

        return ResponseEntity.ok(model);
    }
}
