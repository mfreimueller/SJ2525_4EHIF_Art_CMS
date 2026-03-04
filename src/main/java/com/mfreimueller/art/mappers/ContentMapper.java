package com.mfreimueller.art.mappers;

import com.mfreimueller.art.domain.AudioContent;
import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.dto.AudioContentDto;
import com.mfreimueller.art.dto.ContentDto;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { CreatorMapper.class })
public interface ContentMapper {
    @SubclassMapping(source = AudioContent.class, target = AudioContentDto.class)
    ContentDto toDto(Content content);
}
