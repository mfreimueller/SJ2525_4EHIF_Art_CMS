package com.mfreimueller.art.richtypes;

import jakarta.persistence.Embeddable;

@Embeddable
public record Source(String filePath, LinkType linkType) {

    public enum LinkType {
        Absolute,
        Relative,
        Url
    }
}
