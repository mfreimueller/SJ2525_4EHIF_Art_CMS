package com.mfreimueller.art.presentation.assembler;

import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.dto.ContentDto;
import com.mfreimueller.art.dto.PointOfInterestDto;
import com.mfreimueller.art.mappers.ContentMapper;
import com.mfreimueller.art.mappers.PointOfInterestMapper;
import com.mfreimueller.art.presentation.ContentController;
import com.mfreimueller.art.presentation.PointOfInterestController;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
@RequiredArgsConstructor
public class ContentModelAssembler implements RepresentationModelAssembler<Content, EntityModel<ContentDto>> {

    private final ContentMapper mapper;

    @Override
    public EntityModel<ContentDto> toModel(Content content) {
        var id = content.getId();
        var dto = mapper.toDto(content);

        var self = linkTo(methodOn(ContentController.class).getContent(id))
                .withSelfRel();

        var collection = linkTo(methodOn(ContentController.class).getContent(null)).withRel("collection");

        return EntityModel.of(dto, self, collection);
    }
}
