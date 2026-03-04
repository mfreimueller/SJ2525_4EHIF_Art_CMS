package com.mfreimueller.art.mappers;

import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.dto.ContentDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContentMapper {
    ContentDto toDto(Content content);

    List<ContentDto> toDtos(List<Content> contents);
}
