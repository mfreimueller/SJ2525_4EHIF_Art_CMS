package com.mfreimueller.art.mappers;

import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.domain.VisitHistory;
import com.mfreimueller.art.domain.Visitor;
import com.mfreimueller.art.dto.VisitHistoryDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = SpringMapperConfig.class, uses = { VisitorMapper.class })
public interface VisitHistoryMapper {
    VisitHistoryDto toDto(VisitHistory visitHistory);

    List<VisitHistoryDto> toDtos(List<VisitHistory> visitHistories);

    default PointOfInterest.PointOfInterestId map(PointOfInterest value) {
        return value.getId();
    }

    default Visitor.VisitorId map(Visitor value) {
        return value.getId();
    }
}
