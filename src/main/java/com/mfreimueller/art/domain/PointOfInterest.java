package com.mfreimueller.art.domain;

import com.mfreimueller.art.richtypes.Language;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(of = "id")

@Entity
@Table(name = "poi")
public class PointOfInterest extends HistoryBase {

    @EmbeddedId
    private PointOfInterestId id;

    @ElementCollection
    @CollectionTable(name = "poi_localized_title",
            joinColumns = @JoinColumn(name = "poi_id"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "title")
    private Map<Language, String> title;

    @ElementCollection
    @CollectionTable(name = "poi_localized_description",
            joinColumns = @JoinColumn(name = "poi_id"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "description")
    private Map<Language, String> description;

    public record PointOfInterestId(
            @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "poiSeq")
            @SequenceGenerator(name = "poiSeq", sequenceName = "poi_seq", allocationSize = 1)
            @NotNull Long id) {}
}
