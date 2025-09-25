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
@PrimaryKeyJoinColumn(name = "group_id")
@Table(name = "exhibiton")
public class Exhibition extends Group {

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "exhibition_language",
            joinColumns = @JoinColumn(name = "group_id"))
    private Set<Language> languages;

}
