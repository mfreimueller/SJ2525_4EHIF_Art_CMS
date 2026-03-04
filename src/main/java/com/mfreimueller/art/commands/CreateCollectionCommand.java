package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.Creator;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;

@Builder
public record CreateCollectionCommand(Map<String, String> title, @NotNull Creator.CreatorId creatorId) {
}
