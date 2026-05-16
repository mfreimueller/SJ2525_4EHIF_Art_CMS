package com.mfreimueller.art.presentation.assembler;

import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.dto.CollectionDto;
import com.mfreimueller.art.mappers.CollectionMapper;
import com.mfreimueller.art.presentation.CollectionController;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
@RequiredArgsConstructor
public class CollectionModelAssembler implements RepresentationModelAssembler<Collection, EntityModel<CollectionDto>> {

    private final CollectionMapper mapper;

    @Override
    public EntityModel<CollectionDto> toModel(Collection collection) {
        var id = collection.getId().id();
        var dto = mapper.toDto(collection);

        var self = linkTo(methodOn(CollectionController.class).getCollection(id))
                .withSelfRel()
                .andAffordance(afford(methodOn(CollectionController.class).replaceCollection(id, null, null)))
                .andAffordance(afford(methodOn(CollectionController.class).patchCollection(id, null, null)))
                .andAffordance(afford(methodOn(CollectionController.class).deleteCollection(id)));

        var collectionLink = linkTo(methodOn(CollectionController.class).getCollections(null)).withRel("collection");

        return EntityModel.of(dto, self, collectionLink);
    }
}
