package com.mfreimueller.art.mappers;

import com.mfreimueller.art.domain.Collection;
import com.mfreimueller.art.domain.Exhibition;
import com.mfreimueller.art.domain.PointOfInterest;
import com.mfreimueller.art.dto.CollectionDto;
import com.mfreimueller.art.dto.ExhibitionDto;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

import java.util.List;

@Mapper(config = SpringMapperConfig.class, uses = { CreatorMapper.class })
public interface CollectionMapper {
    @SubclassMapping(source = Exhibition.class, target = ExhibitionDto.class)
    CollectionDto toDto(Collection collection);

    List<CollectionDto> toDtos(List<Collection> collections);

    default PointOfInterest.PointOfInterestId map(PointOfInterest value) {
        return value.getId();
    }

    default Collection.CollectionId map(Collection value) {
        return value.getId();
    }
}
