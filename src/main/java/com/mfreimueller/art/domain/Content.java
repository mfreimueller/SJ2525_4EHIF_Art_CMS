package com.mfreimueller.art.domain;

import com.mfreimueller.art.richtypes.Language;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "content_base")
public abstract class Content extends HistoryBase {

    @EmbeddedId
    private ContentId id;

    @ElementCollection
    @CollectionTable(name = "content_localized_description",
            joinColumns = @JoinColumn(name = "content_id"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "description")
    private Map<Language, String> description;

    public record ContentId(
            @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contentSeq")
            @SequenceGenerator(name = "contentSeq", sequenceName = "content_seq", allocationSize = 1)
            @NotNull Long id) {}
}
