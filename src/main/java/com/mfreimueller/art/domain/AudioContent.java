package com.mfreimueller.art.domain;

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

@Entity
@Table(name = "audio_content")
public class AudioContent extends MediaContent {

    @ElementCollection
    @CollectionTable(name = "audio_content_localized_transcription",
            joinColumns = @JoinColumn(name = "content_id"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "transcriptions")
    private Map<Language, Source> transcriptions;

}
