package com.mfreimueller.art.domain;

import com.mfreimueller.art.richtypes.Duration;
import com.mfreimueller.art.richtypes.Language;
import com.mfreimueller.art.richtypes.Source;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
    @CollectionTable(name = "media_content_localized_source",
            joinColumns = @JoinColumn(name = "content_id"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "source")
    private Map<Language, Source> source;

    @ElementCollection
    @CollectionTable(name = "media_content_localized_duration",
            joinColumns = @JoinColumn(name = "content_id"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "duration")
    private Map<Language, Duration> duration;

}
