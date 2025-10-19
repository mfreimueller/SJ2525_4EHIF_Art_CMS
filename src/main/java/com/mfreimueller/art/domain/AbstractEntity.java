package com.mfreimueller.art.domain;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor(access = AccessLevel.PROTECTED)

@MappedSuperclass
@SuperBuilder
@SequenceGenerator(name = "globalSeq", sequenceName = "global_seq", allocationSize = 1)
public abstract class AbstractEntity {
    // intentionally empty, as we only need the sequence generator to be defined once globally
}
