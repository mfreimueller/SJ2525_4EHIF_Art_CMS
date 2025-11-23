package com.mfreimueller.art.domain;

import com.mfreimueller.art.richtypes.Duration;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(of = "id")

@Entity
@PrimaryKeyJoinColumn(
        name = "content_id",
        foreignKey = @ForeignKey(name = "FK_SlideshowContent_Conent")
)
@Table(name = "SlideshowContent")
public class SlideshowContent extends Content {
    
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "SlideshowContent_Slides",
            foreignKey = @ForeignKey(name = "FK_SlideshowContent_Slides")
    )
    @Builder.Default
    private List<Content> slides = new ArrayList<>();

    private Mode mode;

    private Duration speed;

    public enum Mode {
        Manual,
        Auto,
        Hybrid
    }

}
