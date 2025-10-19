package com.mfreimueller.art.persistence.converters;

/**
 * An interface describing a converter that converts a domain layer entity (of whatever kind) to a database datatype.
 * @param <Entity> The entity that is used in the domain layer, examples are enums or rich types.
 * @param <DbValue> The corresponding database value of the mapped column in the database.
 */
public interface AttributeConverter<Entity, DbValue> {
    DbValue convertToDatabaseValue(Entity entity);

    Entity convertToEntity(DbValue dbValue);
}
