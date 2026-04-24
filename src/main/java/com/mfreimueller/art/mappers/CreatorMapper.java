package com.mfreimueller.art.mappers;

import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.dto.ContentDto;
import com.mfreimueller.art.dto.CreatorDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = SpringMapperConfig.class)
public interface CreatorMapper {
    CreatorDto toDto(Creator creator);
}
