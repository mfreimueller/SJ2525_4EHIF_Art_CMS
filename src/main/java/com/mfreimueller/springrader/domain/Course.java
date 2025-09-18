package com.mfreimueller.springrader.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "classes")
public class Course extends AbstractPersistable<Long> {
}
