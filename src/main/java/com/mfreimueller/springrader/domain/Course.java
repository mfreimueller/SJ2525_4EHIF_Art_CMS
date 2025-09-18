package com.mfreimueller.springrader.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "id")

@Entity
@Table(name = "courses")
public class Course extends AbstractPersistable<Long> {

    @NotBlank
    @Size(min = 3, max = 64)
    private String name;

    private Boolean isCollege;

    @OneToMany(cascade = CascadeType.PERSIST)
    private Set<Exam> exams;
}
