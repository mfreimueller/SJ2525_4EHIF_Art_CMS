package com.mfreimueller.art.domain;

import com.mfreimueller.art.richtypes.Language;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(of = "id")

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "Collection")
public class Collection extends HistoryBase {

    @EmbeddedId
    private CollectionId id;

    @ElementCollection
    @CollectionTable(name = "Collection_Titles",
            foreignKey = @ForeignKey(name = "FK_Collection_Titles"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "title")
    private Map<Language, String> title;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "Collection_PointOfInterest",
            joinColumns = @JoinColumn(foreignKey = @ForeignKey(name = "FK_Collection_PointOfInterest_2_PointOfInterest")),
            inverseJoinColumns = @JoinColumn(foreignKey = @ForeignKey(name = "FK_Collection_PointOfInterest_2_Collection"))
    )
    @Builder.Default
    private Set<PointOfInterest> pointsOfInterest = new HashSet<>();

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "Collection_SubCollections",
            foreignKey = @ForeignKey(name = "FK_Collection_SubCollections")
    )
    @Builder.Default
    private Set<Collection> subCollections = new HashSet<>();

    public record CollectionId(
            @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "collectionSeq")
            @SequenceGenerator(name = "collectionSeq", sequenceName = "collectionSeq", allocationSize = 1)
            @NotNull Long id) {}
}
