package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.domain.Creator;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record CreatePointOfInterestCommand(
        Map<String, String> title,
        Map<String, String> description,
        List<Content.ContentId> content,
        @NotNull Creator.CreatorId creatorId
) {
}
