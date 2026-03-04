package com.mfreimueller.art.presentation.assembler;

import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.dto.PointOfInterestDto;
import com.mfreimueller.art.mappers.PointOfInterestMapper;
import com.mfreimueller.art.presentation.PointOfInterestController;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
@RequiredArgsConstructor
public class PointOfInterestModelAssembler implements RepresentationModelAssembler<PointOfInterest, EntityModel<PointOfInterestDto>> {

    private final PointOfInterestMapper mapper;

    @Override
    public EntityModel<PointOfInterestDto> toModel(PointOfInterest poi) {
        var id = poi.getId().id();
        var dto = mapper.toDto(poi);

        var self = linkTo(methodOn(PointOfInterestController.class).getPointOfInterest(id))
                .withSelfRel()
                .andAffordance(afford(methodOn(PointOfInterestController.class).replacePointOfInterest(id, null, null)))
                .andAffordance(afford(methodOn(PointOfInterestController.class).patchPointOfInterest(id, null, null)))
                .andAffordance(afford(methodOn(PointOfInterestController.class).deletePointOfInterest(id)));

        var collection = linkTo(methodOn(PointOfInterestController.class).getPointsOfInterest(null)).withRel("collection");

        return EntityModel.of(dto, self, collection);
    }
}
