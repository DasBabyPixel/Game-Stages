package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface IAttributeHolder<H extends IAttributeHolder<H>> {
    <T> T get(AttributeQuery<? super H, T> attribute);
}
