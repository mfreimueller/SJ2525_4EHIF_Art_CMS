package com.mfreimueller.art.commands;

import lombok.Builder;

@Builder
public record CreateVisitorCommand(String username, String emailAddress) {
}
