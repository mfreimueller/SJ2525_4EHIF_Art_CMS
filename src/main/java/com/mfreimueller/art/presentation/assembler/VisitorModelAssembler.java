package com.mfreimueller.art.presentation.assembler;

import com.mfreimueller.art.domain.Visitor;
import com.mfreimueller.art.dto.VisitorDto;
import com.mfreimueller.art.mappers.VisitorMapper;
import com.mfreimueller.art.presentation.VisitorController;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
@RequiredArgsConstructor
public class VisitorModelAssembler implements RepresentationModelAssembler<Visitor, EntityModel<VisitorDto>> {

    private final VisitorMapper mapper;

    @Override
    public EntityModel<VisitorDto> toModel(Visitor visitor) {
        var id = visitor.getId().id();
        var dto = mapper.toDto(visitor);

        var self = linkTo(methodOn(VisitorController.class).getVisitor(id))
                .withSelfRel()
                .andAffordance(afford(methodOn(VisitorController.class).replaceVisitor(id, null, null)))
                .andAffordance(afford(methodOn(VisitorController.class).patchVisitor(id, null, null)))
                .andAffordance(afford(methodOn(VisitorController.class).deleteVisitor(id)));

        var collection = linkTo(methodOn(VisitorController.class).getVisitors(null)).withRel("collection");

        return EntityModel.of(dto, self, collection);
    }
}
