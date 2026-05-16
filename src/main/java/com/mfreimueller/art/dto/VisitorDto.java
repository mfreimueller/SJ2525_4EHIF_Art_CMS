package com.mfreimueller.art.dto;

import com.mfreimueller.art.domain.Visitor;
import lombok.Builder;

@Builder
public record VisitorDto(Visitor.VisitorId id, String username, String emailAddress) {
}
