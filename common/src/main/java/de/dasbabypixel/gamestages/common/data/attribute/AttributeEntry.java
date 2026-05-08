package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record AttributeEntry<H extends AttributeHolder<? extends H>, T>(Attribute<? super H, T> attribute, T value) {
}
