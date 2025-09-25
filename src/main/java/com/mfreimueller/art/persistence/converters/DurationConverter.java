package com.mfreimueller.art.persistence.converters;

import com.mfreimueller.art.richtypes.Duration;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DurationConverter extends AbstractIntegerRichTypeConverter<Duration> {

    DurationConverter() {
        super(Duration::new);
    }
}
