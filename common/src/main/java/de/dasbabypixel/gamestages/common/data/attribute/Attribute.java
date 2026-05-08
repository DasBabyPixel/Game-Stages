package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface Attribute<H extends AttributeHolder<? extends H>, T> extends AttributeQuery<H, T> {
    @Override
    T get(H holder);
}
