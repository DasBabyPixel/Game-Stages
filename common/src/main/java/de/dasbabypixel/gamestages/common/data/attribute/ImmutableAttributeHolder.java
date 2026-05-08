package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ImmutableAttributeHolder<Self extends ImmutableAttributeHolder<? extends Self>> extends AttributeHolder<Self> {
    <T> T get(ImmutableAttribute<? super Self, T> attribute);
}
