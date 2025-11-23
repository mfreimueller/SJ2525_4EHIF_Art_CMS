package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.Language;
import com.mfreimueller.art.domain.Source;
import com.mfreimueller.art.richtypes.Duration;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;

@Builder
public record PutVideoContentCommand(
        Map<Language, String> description,
        Map<Language, Source> source,
        Map<Language, Duration> duration,
        Map<Language, Source> subtitles,
        @NotNull Creator.CreatorId creatorId
) {
}
