package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.Content;
import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.domain.Language;
import com.mfreimueller.art.domain.SlideshowContent;
import com.mfreimueller.art.richtypes.Duration;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record CreateUpdateSlideshowContentCommand(Map<Language, String> description, @NotNull List<Content.ContentId> slides,
                                                  SlideshowContent.Mode mode, Duration speed,
                                                  Creator.CreatorId creatorId) {
}
