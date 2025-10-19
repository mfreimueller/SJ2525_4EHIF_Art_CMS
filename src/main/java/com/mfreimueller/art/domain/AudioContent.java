package com.mfreimueller.art.domain;

import jakarta.persistence.*;
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
@Table(name = "AudioContent")
public class AudioContent extends MediaContent {

    @ElementCollection
    @CollectionTable(name = "AudioContent_Transcriptions",
            foreignKey = @ForeignKey(name = "FK_AudioContent_Transcriptions"))
    @MapKeyColumn(name = "language_code")
    @Builder.Default
    private Map<Language, Source> transcriptions = new HashMap<>();

}
