package com.mfreimueller.art.presentation.assembler;

import com.mfreimueller.art.domain.Exhibition;
import com.mfreimueller.art.dto.ExhibitionDto;
import com.mfreimueller.art.mappers.CollectionMapper;
import com.mfreimueller.art.presentation.ExhibitionController;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
@RequiredArgsConstructor
public class ExhibitionModelAssembler implements RepresentationModelAssembler<Exhibition, EntityModel<ExhibitionDto>> {

    private final CollectionMapper mapper;

    @Override
    public EntityModel<ExhibitionDto> toModel(Exhibition exhibition) {
        var id = exhibition.getId().id();
        var dto = (ExhibitionDto) mapper.toDto(exhibition);

        var self = linkTo(methodOn(ExhibitionController.class).getExhibition(id))
                .withSelfRel()
                .andAffordance(afford(methodOn(ExhibitionController.class).replaceExhibition(id, null, null)))
                .andAffordance(afford(methodOn(ExhibitionController.class).patchExhibition(id, null, null)))
                .andAffordance(afford(methodOn(ExhibitionController.class).deleteExhibition(id)));

        var collectionLink = linkTo(methodOn(ExhibitionController.class).getExhibitions(null)).withRel("collection");

        return EntityModel.of(dto, self, collectionLink);
    }
}
