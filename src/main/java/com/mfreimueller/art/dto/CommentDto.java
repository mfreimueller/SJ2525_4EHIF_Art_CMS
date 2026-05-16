package com.mfreimueller.art.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CommentDto(String body) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CommentResponse(List<CommentDto> comments) {
    }
}
