package com.mfreimueller.art.dto;

import com.mfreimueller.art.domain.Content;

import java.util.Map;

public record ContentDto(Content.ContentId id, Map<String, String> description) {
}
