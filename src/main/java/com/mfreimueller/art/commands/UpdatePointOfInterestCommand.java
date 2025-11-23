package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.Language;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record UpdatePointOfInterestCommand(
        @NotNull Map<Language, String> title,
        @NotNull Map<Language, String> description,
        List<Content.ContentId> content,
        @NotNull Creator.CreatorId creatorId) {
}
