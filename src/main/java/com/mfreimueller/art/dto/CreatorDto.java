package com.mfreimueller.art.dto;

import com.mfreimueller.art.domain.Creator;

public record CreatorDto(Creator.CreatorId id, String username) {
}
