package com.mfreimueller.art.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.ZonedDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "id")

@Entity
@Table(name = "visitor")
public class Visitor {

    @EmbeddedId
    private VisitorId id;

    @NotBlank
    @Size(min = 3, max = 64)
    private String username;

    @Email
    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "first_visit_ts")
    private ZonedDateTime firstVisitTS;

    @Column(name = "more_recent_visit_ts")
    private ZonedDateTime mostRecentVisitTS;

    public record VisitorId(
            @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "visitorSeq")
            @SequenceGenerator(name = "visitorSeq", sequenceName = "visitor_seq", allocationSize = 1)
            @NotNull Long id) {}
}
