package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

import java.util.function.Function;
import java.util.function.Supplier;

@NullMarked
public class Attribute<H extends AttributeHolder<? extends H>, T> implements AttributeQuery<H, T> {
    private final Function<H, T> defaultValue;

    public Attribute(Function<H, T> defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Attribute(Supplier<T> defaultValue) {
        this(ignore -> defaultValue.get());
    }

    @Override
    public T get(H holder) {
        return holder.get(this);
    }

    public T supply(H holder) {
        return defaultValue.apply(holder);
    }
}
