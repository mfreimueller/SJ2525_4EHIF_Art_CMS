package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.Source;
import com.mfreimueller.art.richtypes.Duration;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;

@Builder
public record PutVideoContentCommand(
        Map<String, String> description,
        Map<String, Source> source,
        Map<String, Duration> duration,
        Map<String, Source> subtitles,
        @NotNull Creator.CreatorId creatorId
) {
}
