package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface IAttributeHolder<Self extends IAttributeHolder<Self>> {
    default <T> T get(AttributeQuery<? super Self, T> attribute) {
        return attribute.get(self());
    }

    @SuppressWarnings("unchecked")
    default Self self() {
        return (Self) this;
    }
}
