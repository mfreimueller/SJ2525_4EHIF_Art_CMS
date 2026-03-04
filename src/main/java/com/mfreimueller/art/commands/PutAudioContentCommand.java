package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.Source;
import com.mfreimueller.art.richtypes.Duration;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;

@Builder
public record PutAudioContentCommand(
        Map<String, String> description,
        Map<String, Source> source,
        Map<String, Duration> duration,
        Map<String, Source> transcriptions,
        @NotNull Creator.CreatorId creatorId
) {
}
