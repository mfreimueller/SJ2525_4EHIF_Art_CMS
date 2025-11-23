package com.mfreimueller.art.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "id")

@Entity
@Table(name = "Visitor")
public class Visitor extends AbstractEntity {
    // TODO: add way to uniquely identify identical visitors across different visits if they don't provide username/password

    @EmbeddedId
    private VisitorId id;

    @NotBlank
    @Size(min = 3, max = 64)
    private String username;

    @Email
    @Column(name = "email_address")
    private String emailAddress; // TODO: create rich type

    @OneToMany(mappedBy = "visitor", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<VisitHistory> visitHistories = new ArrayList<>();

    public record VisitorId(
            @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "globalSeq")
            @NotNull Long id) {}
}
