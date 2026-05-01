package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

import java.util.Collection;

@NullMarked
public interface AttributeHolder<Self extends AttributeHolder<Self>> extends IAttributeHolder<Self> {
    <T> T get(Attribute<? super Self, T> attribute);

    Collection<AttributeEntry<? super Self, ?>> attributes();
}
