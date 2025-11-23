package com.mfreimueller.art.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@SuperBuilder
@Getter
@Setter

@MappedSuperclass
public class HistoryBase extends AbstractEntity {
    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "creator_id",
            foreignKey = @ForeignKey(name = "FK_HistoryBase_CreatedBy")
    )
    private Creator createdBy;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "updater_id",
            foreignKey = @ForeignKey(name = "FK_HistoryBase_UpdatedBy")
    )
    private Creator updatedBy;
}
