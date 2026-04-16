package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

import java.util.Collection;

@NullMarked
public interface TypedGameContent extends GameContent {
    GameContentType<?> type();

    Iterable<? extends Object> content();

    Collection<? extends Object> contentCollection();

    boolean isEmpty();
}
