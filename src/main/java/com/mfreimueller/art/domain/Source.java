package com.mfreimueller.art.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record Source(String filePath, LinkType linkType) {

    public enum LinkType {
        Absolute,
        Relative,
        Url
    }
}
