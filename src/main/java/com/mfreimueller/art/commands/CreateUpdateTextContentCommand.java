package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.Language;
import lombok.Builder;

import java.util.Map;

@Builder
public record CreateUpdateTextContentCommand(
        Map<Language, String> description,
        Map<Language, String> shortText,
        Map<Language, String> longText
) {
}
