package de.dasbabypixel.gamestages.common.data.attribute;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record AttributeEntry<H extends AttributeHolder<H>, T>(Attribute<H, T> attribute, T value) {
}
