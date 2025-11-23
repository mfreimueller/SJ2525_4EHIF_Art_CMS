package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.Language;
import lombok.Builder;

import java.util.Map;
import java.util.Set;

@Builder
public record UpdateCollectionCommand(Map<Language, String> title) {
}
