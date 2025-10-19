package com.mfreimueller.art.domain;

import com.mfreimueller.art.richtypes.Duration;
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

@PrimaryKeyJoinColumn(name = "content_id")
@MappedSuperclass
public class MediaContent extends Content {

    @ElementCollection
    @CollectionTable(name = "MediaContent_Sources",
            foreignKey = @ForeignKey(name = "FK_MediaContent_Sources"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "source")
    @Builder.Default
    private Map<Language, Source> source = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "MediaContent_Durations",
            foreignKey = @ForeignKey(name = "FK_MediaContent_Durations"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "duration")
    @Builder.Default
    private Map<Language, Duration> duration = new HashMap<>();

}
