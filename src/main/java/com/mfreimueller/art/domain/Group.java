package com.mfreimueller.art.domain;

import com.mfreimueller.art.richtypes.Language;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(of = "id")

@Entity
@Table(name = "group")
public class Group extends HistoryBase {

    @EmbeddedId
    private GroupId id;

    @ElementCollection
    @CollectionTable(name = "group_localized_title",
            joinColumns = @JoinColumn(name = "group_id"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "title")
    private Map<Language, String> title;

    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<PointOfInterest> pointsOfInterest;

    @OneToMany(cascade = CascadeType.PERSIST)
    private Set<Group> subGroups;

    public record GroupId(
            @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "groupSeq")
            @SequenceGenerator(name = "groupSeq", sequenceName = "group_seq", allocationSize = 1)
            @NotNull Long id) {}
}
