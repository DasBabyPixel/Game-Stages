package de.dasbabypixel.gamestages.common.event;

import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public record EventListener<Event>(int order, Consumer<Event> executor) {
}
