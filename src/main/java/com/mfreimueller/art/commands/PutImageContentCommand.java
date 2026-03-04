package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;

@Builder
public record PutImageContentCommand(Map<String, String> description, Source source, @NotNull Creator.CreatorId creatorId) {
}
