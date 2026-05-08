package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface AttributeQuery<H extends AttributeHolder<? extends H>, T> {
    T get(H holder);
}
