package com.mfreimueller.art.dto;

import com.mfreimueller.art.domain.Source;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@Setter
@SuperBuilder

@AllArgsConstructor
@NoArgsConstructor
public class AudioContentDto extends ContentDto {
        private Map<String, String> description;
        private Map<String, Source> transcriptions;
}
