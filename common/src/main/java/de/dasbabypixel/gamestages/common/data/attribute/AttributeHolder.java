package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

import java.util.Collection;

@NullMarked
public interface AttributeHolder<Self extends AttributeHolder<? extends Self>> {
    default <T> T get(Attribute<? super Self, T> attribute) {
        return attribute.get(self());
    }

    Collection<AttributeEntry<? super Self, ?>> attributes();

    @SuppressWarnings("unchecked")
    default Self self() {
        return (Self) this;
    }
}
