package com.mfreimueller.art.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(of = "id")

@Entity
@Table(name = "point_of_interest")
public class PointOfInterest extends HistoryBase {

    @EmbeddedId
    private PointOfInterestId id;

    @NotBlank
    @Size(min = 3, max = 64)
    private String name;

    // @OneToMany(cascade = CascadeType.PERSIST)
    // private Set<HistoryBase> historyBases;

    public record PointOfInterestId(
            @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "poiSeq")
            @SequenceGenerator(name = "poiSeq", sequenceName = "poi_seq", allocationSize = 1)
            @NotNull Long id) {}
}
