package com.mfreimueller.art.foundation;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Component
public class DateTimeFactory {

    public ZonedDateTime now() {
        return ZonedDateTime.now();
    }

    public LocalDate today() {
        return LocalDate.now();
    }

    public LocalDate currentTime() {
        return LocalDate.now();
    }

}
