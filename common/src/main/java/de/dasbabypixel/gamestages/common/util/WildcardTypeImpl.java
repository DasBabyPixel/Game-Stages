package de.dasbabypixel.gamestages.common.util;

import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

@NullMarked
public record WildcardTypeImpl(Type[] getUpperBounds, Type[] getLowerBounds) implements WildcardType {
    public static final WildcardTypeImpl NO_BOUNDS = new WildcardTypeImpl(new Type[0], new Type[0]);

    public static WildcardTypeImpl createWithUpperBounds(Type... upperBounds) {
        return new WildcardTypeImpl(upperBounds, new Type[0]);
    }
}
