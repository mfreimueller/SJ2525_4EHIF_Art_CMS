package com.mfreimueller.art.presentation;

import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.dto.ContentDto;
import com.mfreimueller.art.dto.KeysetPaged;
import com.mfreimueller.art.presentation.assembler.ContentModelAssembler;
import com.mfreimueller.art.service.ContentService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.SlicedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<EntityModel<ContentDto>> getContent(@PathParam("id") Long id) {
        throw new NotImplementedException();
    }

    @GetMapping
    public ResponseEntity<SlicedModel<EntityModel<ContentDto>>> getContents(
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "20") int pageSize
            ) {
        logEnter(log);

        // FIXME: this needs to be reworked to support keyset pagination. Currently it's a mess.

        var content = service.getPaged(lastId, pageSize);
        var items = content.map(assembler::toModel).stream().toList();

        var self = linkTo(methodOn(ContentController.class).getContents(lastId, pageSize)).withSelfRel();

        var metadata = new SlicedModel.SliceMetadata(
                content.getSize(),
                content.getNumber()
        );

        var model = SlicedModel.of(items, metadata, self);

        if (content.hasPrevious()) {
            var prev = linkTo(methodOn(ContentController.class).getContents(lastId - pageSize, pageSize))
                    .withRel("prev");
            model.add(prev);
        }

        if (content.hasNext()) {
            var next = linkTo(methodOn(ContentController.class).getContents(lastId + pageSize, pageSize))
                    .withRel("next");
            model.add(next);
        }

        log.info("Retrieved {} Content entities (keyset paging with slices)", content.getSize());
        logExit(log);

        return ResponseEntity.ok(model);
    }
}
