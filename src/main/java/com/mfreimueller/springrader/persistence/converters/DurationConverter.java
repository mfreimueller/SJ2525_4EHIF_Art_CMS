package com.mfreimueller.springrader.persistence.converters;

import com.mfreimueller.springrader.richtypes.Duration;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DurationConverter extends AbstractIntegerRichTypeConverter<Duration> {

    DurationConverter() {
        super(Duration::new);
    }
}
