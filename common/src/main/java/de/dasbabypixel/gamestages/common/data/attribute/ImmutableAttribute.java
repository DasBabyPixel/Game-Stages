package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class ImmutableAttribute<H extends ImmutableAttributeHolder<H>, T> implements AttributeQuery<H, T> {
    @Override
    public T get(H holder) {
        return holder.get(this);
    }
}
