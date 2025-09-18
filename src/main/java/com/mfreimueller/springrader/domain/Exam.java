package com.mfreimueller.springrader.domain;

import com.mfreimueller.springrader.richtypes.Duration;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.ZonedDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "id")

@Entity
@Table(name = "tests")
public class Exam extends AbstractPersistable<Long> {

    private ZonedDateTime date;

    @Column(columnDefinition = "check (width > 0 AND width <= 250)")
    private Duration duration;

}
