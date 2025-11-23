package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.Language;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;

@Builder
public record PutTextContentCommand(
        Map<Language, String> description,
        Map<Language, String> shortText,
        Map<Language, String> longText,
        @NotNull Creator.CreatorId creatorId
) {
}
