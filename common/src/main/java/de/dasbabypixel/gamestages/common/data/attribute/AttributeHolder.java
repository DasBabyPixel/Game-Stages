package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

import java.util.Collection;

@NullMarked
public interface AttributeHolder<H extends AttributeHolder<H>> {
    <T> T get(Attribute<? super H, T> attribute);

    <T> T get(AttributeQuery<? super H, T> attribute);

    Collection<AttributeEntry<? super H, ?>> attributes();
}
