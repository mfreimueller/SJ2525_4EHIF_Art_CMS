package com.mfreimueller.art.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record Language(String code, String name) {

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Language lang) {
            return lang.code.equals(code);
        }

        return false;
    }
}
