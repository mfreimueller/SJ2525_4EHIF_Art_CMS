package com.mfreimueller.art.domain;

import com.mfreimueller.art.richtypes.Language;
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
@PrimaryKeyJoinColumn(name = "content_id")
@Table(name = "text_content")
public class TextContent extends Content {
    
    @ElementCollection
    @CollectionTable(name = "content_localized_short_text",
            joinColumns = @JoinColumn(name = "content_id"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "shortText")
    private Map<Language, String> shortText;

    @ElementCollection
    @CollectionTable(name = "content_localized_long_text",
            joinColumns = @JoinColumn(name = "content_id"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "longText")
    private Map<Language, String> longText;

}
