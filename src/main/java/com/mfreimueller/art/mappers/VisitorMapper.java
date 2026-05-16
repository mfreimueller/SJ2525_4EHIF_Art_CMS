package com.mfreimueller.art.mappers;

import com.mfreimueller.art.domain.Visitor;
import com.mfreimueller.art.dto.VisitorDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = SpringMapperConfig.class)
public interface VisitorMapper {
    VisitorDto toDto(Visitor visitor);

    List<VisitorDto> toDtos(List<Visitor> visitors);
}
