package com.mfreimueller.springrader.domain;

import com.mfreimueller.springrader.richtypes.Duration;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.ZonedDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "id")

// note to myself: we don't use AbstractPersistable, because we want to use different
// types of primary keys (i.e. not only basic types)
@Entity
@Table(name = "exams")
public class Exam {

    // note to myself: we use embedded id to support complex types as ids
    @EmbeddedId
    private ExamId id;

    @Column(name = "date_ts")
    private ZonedDateTime dateTS;

    @Column(columnDefinition = "integer check (duration > 0 AND duration <= 250)")
    private Duration duration;

    // note to myself: strongly typed to uniquely identify the identifier type of Exam
    // which makes it obvious in code, which identifier we are working with (cmp ex negativo: using only
    // Long, which makes it hard to distinguish between ids of Exam and Course etc.
    public record ExamId(
            @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "examSeq")
            @SequenceGenerator(name = "examSeq", sequenceName = "exam_seq",  allocationSize = 1)
            @NotNull Long id){}
}
