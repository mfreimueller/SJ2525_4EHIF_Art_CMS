package com.mfreimueller.art.presentation.assembler;

import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.dto.CreatorDto;
import com.mfreimueller.art.mappers.CreatorMapper;
import com.mfreimueller.art.presentation.CreatorController;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
@RequiredArgsConstructor
public class CreatorModelAssembler implements RepresentationModelAssembler<Creator, EntityModel<CreatorDto>> {

    private final CreatorMapper mapper;

    @Override
    public EntityModel<CreatorDto> toModel(Creator creator) {
        var id = creator.getId().id();
        var dto = mapper.toDto(creator);

        var self = linkTo(methodOn(CreatorController.class).getCreator(id))
                .withSelfRel()
                .andAffordance(afford(methodOn(CreatorController.class).replaceCreator(id, null, null)))
                .andAffordance(afford(methodOn(CreatorController.class).patchCreator(id, null, null)))
                .andAffordance(afford(methodOn(CreatorController.class).deleteCreator(id)));

        var collection = linkTo(methodOn(CreatorController.class).getCreators(null)).withRel("collection");

        return EntityModel.of(dto, self, collection);
    }
}
