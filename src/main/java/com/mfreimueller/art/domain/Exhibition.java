package com.mfreimueller.art.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(of = "id")

@Entity
@PrimaryKeyJoinColumn(name = "group_id")
@Table(name = "Exhibiton")
public class Exhibition extends Collection {

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "Exhibition_Languages",
            foreignKey = @ForeignKey(name = "FK_Exhibition_Languages"))
    @Builder.Default
    private Set<Language> languages = new HashSet<>();

}
