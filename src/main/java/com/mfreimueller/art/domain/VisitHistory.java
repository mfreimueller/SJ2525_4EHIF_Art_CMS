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
@Table(name = "VisitHistory")
public class VisitHistory extends AbstractEntity {

    @EmbeddedId
    private VisitHistoryId id;

    private Duration duration; // FIXME: Duration doesn't make sense here, as it is limited to 250 seconds

    @Column(name = "visited_on")
    private ZonedDateTime visitedOn;

    @ManyToMany
    @Builder.Default
    @JoinTable(
            name = "VisitHistory_PointOfInterest",
            joinColumns = @JoinColumn(foreignKey = @ForeignKey(name = "FK_VisitHistory_PointOfInterest_2_PointOfInterest")),
            inverseJoinColumns = @JoinColumn(foreignKey = @ForeignKey(name = "VisitHistory_PointOfInterest_2_VisitHistory"))
    )
    private List<PointOfInterest> pointsOfInterest = new ArrayList<>(); // note: we use a list, as a list is ordered.

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "visitor_id",
            foreignKey = @ForeignKey(name = "FK_VisitHistory_Visitor")
    )
    private Visitor visitor;

    public record VisitHistoryId(
            @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "globalSeq")
            @NotNull Long id) {}
}
