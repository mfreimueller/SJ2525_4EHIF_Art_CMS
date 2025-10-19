package com.mfreimueller.art.domain;

import com.mfreimueller.art.richtypes.Language;
import com.mfreimueller.art.richtypes.Source;
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
@Table(name = "VideoContent")
public class VideoContent extends MediaContent {

    @ElementCollection
    @CollectionTable(name = "VideoContent_Sources",
            foreignKey = @ForeignKey(name = "FK_VideoContent_Sources"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "subtitles")
    @Builder.Default
    private Map<Language, Source> subtitles = new HashMap<>();

}
