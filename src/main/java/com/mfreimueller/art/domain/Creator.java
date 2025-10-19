package com.mfreimueller.art.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "id")

@Entity
@Table(name = "Creator")
public class Creator {

    @EmbeddedId
    private CreatorId id;

    @NotBlank
    @Size(min = 3, max = 64)
    private String username;

    private Role role;

    public record CreatorId(
            @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "creatorSeq")
            @SequenceGenerator(name = "creatorSeq", sequenceName = "creator_seq", allocationSize = 1)
            @NotNull Long id) {}

    public enum Role {
        Viewer,
        Editor,
        Admin;
    }
}
