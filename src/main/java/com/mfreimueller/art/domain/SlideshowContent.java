package com.mfreimueller.art.domain;

import com.mfreimueller.art.richtypes.Duration;
import com.mfreimueller.art.richtypes.Language;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(of = "id")

@Entity
@PrimaryKeyJoinColumn(name = "content_id")
@Table(name = "slideshow_content")
public class SlideshowContent extends Content {
    
    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Content> slides;

    private Mode mode;

    private Duration speed;

    public enum Mode {
        Manual,
        Auto,
        Hybrid
    }

}
