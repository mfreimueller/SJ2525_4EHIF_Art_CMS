package com.mfreimueller.art.domain;

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

    @OneToMany(mappedBy = "parentCollection", cascade = CascadeType.PERSIST)
    @Builder.Default
    private Set<Collection> subCollections = new HashSet<>();

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "parent_id",
            foreignKey = @ForeignKey(name = "FK_Collection_Parent")
    )
    private Collection parentCollection;

    /**
     * Gets the top most collection from the parent collections, or returns
     * this instance if no parent collection is found.
     * @return The collection that is the parent to all collections connected to this collection.
     */
    public Collection getTopCollection() {
        if (parentCollection == null) {
            return this;
        }

        return parentCollection.getTopCollection();
    }

    /**
     * Recursively searches the subcollections for the given collection.
     * @param collection The collection to find.
     * @return True if the collection was found in any subcollection, otherwise false.
     */
    public boolean contains(Collection collection) {
        if (subCollections.contains(collection)) {
            return true;
        }

        return subCollections.stream().anyMatch(s -> s.contains(collection));
    }

    public record CollectionId(
            @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "globalSeq")
            @NotNull Long id) {}
}
