package com.mfreimueller.art.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(of = "id")

@Entity
@PrimaryKeyJoinColumn(
        name = "content_id",
        foreignKey = @ForeignKey(name = "FK_ImageContent_Content")
)
@Table(name = "ImageContent")
public class ImageContent extends Content {

    @Embedded
    private Source source;

}
