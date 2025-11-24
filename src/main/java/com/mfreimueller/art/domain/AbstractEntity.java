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
public abstract class AbstractEntity {
    @Version
    private Long version;

    @Override
    @SuppressWarnings("JavaReflectionMemberAccess")
    public boolean equals(Object obj) {
        if (obj instanceof AbstractEntity ae) {
            // some black magic... until I find a better solution
            try {
                var getIdMethod = AbstractEntity.class.getDeclaredMethod("getId");

                var objId = getIdMethod.invoke(ae);
                var thisId = getIdMethod.invoke(this);

                return thisId.equals(objId);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return false;
            }
        }

        return false;
    }
}
