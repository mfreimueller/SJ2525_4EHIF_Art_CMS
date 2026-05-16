package com.mfreimueller.art.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@Setter
@SuperBuilder

@AllArgsConstructor
@NoArgsConstructor
public class TextContentDto extends ContentDto {
    private Map<String, String> description;
    private Map<String, String> shortText;
    private Map<String, String> longText;
}
