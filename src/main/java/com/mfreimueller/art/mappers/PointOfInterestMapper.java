package com.mfreimueller.art.mappers;

import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.dto.PointOfInterestDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = { ContentMapper.class })
public interface PointOfInterestMapper {
    PointOfInterestDto toDto(PointOfInterest poi);

    List<PointOfInterestDto> toDtos(List<PointOfInterest> pois);
}
