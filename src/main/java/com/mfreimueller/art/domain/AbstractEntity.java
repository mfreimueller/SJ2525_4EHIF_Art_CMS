package com.mfreimueller.art.domain;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PROTECTED)

@MappedSuperclass
@SuperBuilder
@SequenceGenerator(name = "globalSeq", sequenceName = "global_seq", allocationSize = 1)
@Slf4j
public abstract class AbstractEntity<T> {
    @Version
    private Long version;

    public abstract T getId();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractEntity ae) {
            return getId().equals(ae.getId());
        }

        return false;
    }
}
