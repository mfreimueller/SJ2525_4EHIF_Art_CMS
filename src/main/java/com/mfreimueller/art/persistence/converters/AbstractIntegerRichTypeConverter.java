package com.mfreimueller.art.persistence.converters;

import com.mfreimueller.art.richtypes.SingleValue;
import jakarta.persistence.AttributeConverter;

import java.util.function.Function;

public abstract class AbstractIntegerRichTypeConverter<T extends SingleValue<Integer>> implements AttributeConverter<T, Integer> {
    private final Function<Integer, T> constructor;

    AbstractIntegerRichTypeConverter(Function<Integer, T> constructor) {
        this.constructor = constructor;
    }

    @Override
    public Integer convertToDatabaseColumn(T entity) {
        return entity == null ? null : entity.value();
    }

    @Override
    public T convertToEntityAttribute(Integer integer) {
        return integer == null ? null : constructor.apply(integer);
    }
}
