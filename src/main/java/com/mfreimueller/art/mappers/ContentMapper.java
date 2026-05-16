package com.mfreimueller.art.mappers;

import com.mfreimueller.art.domain.*;
import com.mfreimueller.art.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

import java.util.List;

@Mapper(config = SpringMapperConfig.class, uses = { CreatorMapper.class })
public interface ContentMapper {
    @SubclassMapping(source = AudioContent.class, target = AudioContentDto.class)
    @SubclassMapping(source = TextContent.class, target = TextContentDto.class)
    @SubclassMapping(source = ImageContent.class, target = ImageContentDto.class)
    @SubclassMapping(source = VideoContent.class, target = VideoContentDto.class)
    @SubclassMapping(source = SlideshowContent.class, target = SlideshowContentDto.class)
    ContentDto toDto(Content content);
}
