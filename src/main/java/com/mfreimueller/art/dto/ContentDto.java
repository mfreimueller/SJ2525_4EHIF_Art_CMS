package com.mfreimueller.art.dto;

import com.mfreimueller.art.domain.Content;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@SuperBuilder
public class ContentDto {
    private Content.ContentId id;
    @Builder.Default
    private Map<String, String> description = new HashMap<>();
}
