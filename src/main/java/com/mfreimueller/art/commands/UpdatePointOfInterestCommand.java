package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.Language;
import lombok.Builder;

import java.util.Map;

@Builder
public record UpdatePointOfInterestCommand(
        Map<Language, String> title,
        Map<Language, String> description) {
}
