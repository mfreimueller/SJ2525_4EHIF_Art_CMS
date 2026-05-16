package com.mfreimueller.art.presentation.assembler;

import com.mfreimueller.art.domain.VisitHistory;
import com.mfreimueller.art.dto.VisitHistoryDto;
import com.mfreimueller.art.mappers.VisitHistoryMapper;
import com.mfreimueller.art.presentation.VisitHistoryController;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
@RequiredArgsConstructor
public class VisitHistoryModelAssembler implements RepresentationModelAssembler<VisitHistory, EntityModel<VisitHistoryDto>> {

    private final VisitHistoryMapper mapper;

    @Override
    public EntityModel<VisitHistoryDto> toModel(VisitHistory visitHistory) {
        var id = visitHistory.getId().id();
        var dto = mapper.toDto(visitHistory);

        var self = linkTo(methodOn(VisitHistoryController.class).getVisitHistory(id))
                .withSelfRel();

        var collection = linkTo(methodOn(VisitHistoryController.class).getVisitHistories(null)).withRel("collection");

        return EntityModel.of(dto, self, collection);
    }
}
