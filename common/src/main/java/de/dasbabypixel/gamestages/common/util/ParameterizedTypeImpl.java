package de.dasbabypixel.gamestages.common.util;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@NullMarked
public record ParameterizedTypeImpl(Type[] getActualTypeArguments, Type getRawType,
                                    @Nullable Type getOwnerType) implements ParameterizedType {
    public ParameterizedTypeImpl(Type rawType, Type... actualTypeArguments) {
        this(actualTypeArguments, rawType, null);
    }
}
