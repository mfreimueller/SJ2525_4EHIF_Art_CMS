package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.Creator;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;
import java.util.Set;

@Builder
public record CreateExhibitionCommand(Map<String, String> title, Set<String> languages, @NotNull Creator.CreatorId creatorId) {
}
