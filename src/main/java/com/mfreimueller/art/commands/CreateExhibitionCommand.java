package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.Language;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;
import java.util.Set;

@Builder
public record CreateExhibitionCommand(Map<Language, String> title, Set<Language> languages, @NotNull Creator.CreatorId creatorId) {
}
