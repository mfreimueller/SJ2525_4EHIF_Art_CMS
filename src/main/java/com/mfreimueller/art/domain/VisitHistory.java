package com.mfreimueller.art.domain;

import com.mfreimueller.art.richtypes.Duration;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "id")

@Entity
@Table(name = "visit_history")
public class VisitHistory {

    @EmbeddedId
    private VisitHistoryId id;

    private Duration duration;

    @Column(name = "visited_on")
    private ZonedDateTime visitedOn;

    @ManyToMany
    @Builder.Default
    private List<PointOfInterest> pointsOfInterest = new ArrayList<>();

    public record VisitHistoryId(
            @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "visitHistorySeq")
            @SequenceGenerator(name = "visitHistorySeq", sequenceName = "visit_history__seq", allocationSize = 1)
            @NotNull Long id) {}
}
