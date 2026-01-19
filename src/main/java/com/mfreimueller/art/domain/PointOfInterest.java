package com.mfreimueller.art.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(of = "id")

@Entity
@Table(name = "PointOfInterest")
public class PointOfInterest extends HistoryBase {

    @EmbeddedId
    private PointOfInterestId id;

    @ElementCollection
    @CollectionTable(name = "PointOfInterest_Titles",
            foreignKey = @ForeignKey(name = "FK_PointOfInterest_Titles"))
    @Column(name = "title")
    @Builder.Default
    private Map<String, String> title = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "PointOfInterest_Descriptions",
            foreignKey = @ForeignKey(name = "FK_PointOfInterest_Descriptions"))
    @Column(name = "description")
    @Builder.Default
    private Map<String, String> description = new HashMap<>();

    @ManyToMany
    @Builder.Default
    @JoinTable(
            name = "PointOfInterest_Content",
            joinColumns = @JoinColumn(foreignKey = @ForeignKey(name = "FK_PointOfInterest_Content_2_PointOfInterest")),
            inverseJoinColumns = @JoinColumn(foreignKey = @ForeignKey(name = "FK_PointOfInterest_Content_2_Content"))
    )
    private List<Content> content = new ArrayList<>(); // note: we use a list, as a list is ordered.

    public record PointOfInterestId(
            @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "globalSeq")
            @NotNull Long id) {}
}
