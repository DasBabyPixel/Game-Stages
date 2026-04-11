package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

import java.util.Collection;

@NullMarked
public interface TypedGameContent extends GameContent {
    GameContentType<?> type();

    Collection<? extends Object> content();

    boolean isEmpty();
}
