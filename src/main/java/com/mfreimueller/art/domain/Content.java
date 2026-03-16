package com.mfreimueller.art.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(of = "id")

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "ContentBase")
public abstract class Content extends HistoryBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "globalSeq")
    @NotNull
    private Long id;

    @ElementCollection
    @CollectionTable(name = "Content_LocalizedDescriptions",
            foreignKey = @ForeignKey(name = "FK_Content_LocalizedDescriptions"))
    @Column(name = "description")
    @Builder.Default
    private Map<String, String> description = new HashMap<>();
}
