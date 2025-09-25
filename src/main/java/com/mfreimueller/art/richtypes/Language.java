package com.mfreimueller.art.richtypes;

import jakarta.persistence.Embeddable;

@Embeddable
public record Language(String value) {
}
