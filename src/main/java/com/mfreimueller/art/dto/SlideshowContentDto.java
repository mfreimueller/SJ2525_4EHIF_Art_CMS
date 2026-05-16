package com.mfreimueller.art.dto;

import com.mfreimueller.art.domain.SlideshowContent;
import com.mfreimueller.art.richtypes.Duration;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@SuperBuilder

@AllArgsConstructor
@NoArgsConstructor
public class SlideshowContentDto extends ContentDto {
    private Map<String, String> description;
    private List<ContentDto> slides;
    private SlideshowContent.Mode mode;
    private Duration speed;
}
