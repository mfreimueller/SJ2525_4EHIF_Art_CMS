package com.mfreimueller.art.richtypes;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Duration(@Column(name = "duration") Integer value) implements SingleValue<Integer> {

    public Duration {
        if (value == null || value < 0 || value > 250) {
            throw new IllegalArgumentException(("%s requires a non-null positive value smaller than 251, but received: ").formatted(getClass().getSimpleName()) + value);
        }
    }

    public static Duration of(Integer value) {
        return new Duration(value);
    }
}
