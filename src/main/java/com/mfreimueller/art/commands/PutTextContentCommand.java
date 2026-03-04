package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.Creator;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;

@Builder
public record PutTextContentCommand(
        Map<String, String> description,
        Map<String, String> shortText,
        Map<String, String> longText,
        @NotNull Creator.CreatorId creatorId
) {
}
