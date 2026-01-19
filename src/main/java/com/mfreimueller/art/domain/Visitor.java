package com.mfreimueller.art.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE)
    @Column(name = "email_address")
    private String emailAddress;

    @OneToMany(mappedBy = "visitor", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<VisitHistory> visitHistories = new ArrayList<>();

    public record VisitorId(
            @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "globalSeq")
            @NotNull Long id) {}
}
