package com.mfreimueller.art.commands;

import com.mfreimueller.art.domain.Creator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateCreatorCommand(@NotNull @NotBlank String username, @NotNull Creator.Role role) {
}
