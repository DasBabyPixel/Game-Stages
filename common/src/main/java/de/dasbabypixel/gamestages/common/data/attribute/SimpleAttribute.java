package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class SimpleAttribute<H extends SimpleAttributeHolder<? extends H>, T> implements Attribute<H, T> {
    @Override
    public T get(H holder) {
        return holder.get(this);
    }
}
