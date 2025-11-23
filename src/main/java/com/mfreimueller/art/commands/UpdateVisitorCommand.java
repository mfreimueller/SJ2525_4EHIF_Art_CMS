package com.mfreimueller.art.commands;

import lombok.Builder;

@Builder
public record UpdateVisitorCommand(String username, String email) {
}
