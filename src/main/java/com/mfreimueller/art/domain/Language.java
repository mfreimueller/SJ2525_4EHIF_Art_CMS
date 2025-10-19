package com.mfreimueller.art.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record Language(String code, String name) {
}
