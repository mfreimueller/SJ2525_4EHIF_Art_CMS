package com.mfreimueller.art.dto;

import com.mfreimueller.art.domain.Source;
import com.mfreimueller.art.richtypes.Duration;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@Setter
@SuperBuilder

@AllArgsConstructor
@NoArgsConstructor
public class VideoContentDto extends ContentDto {
    private Map<String, String> description;
    private Map<String, Source> source;
    private Map<String, Duration> duration;
    private Map<String, Source> subtitles;
}
