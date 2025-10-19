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
@PrimaryKeyJoinColumn(name = "content_id")
@Table(name = "TextContent")
public class TextContent extends Content {
    
    @ElementCollection
    @CollectionTable(name = "TextContent_ShortTexts",
            foreignKey = @ForeignKey(name = "FK_TextContent_ShortTexts"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "shortText")
    @Builder.Default
    private Map<Language, String> shortText = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "TextContent_LongTexts",
            foreignKey = @ForeignKey(name = "FK_TextContent_LongTexts"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "longText")
    @Builder.Default
    private Map<Language, String> longText = new HashMap<>();

}
