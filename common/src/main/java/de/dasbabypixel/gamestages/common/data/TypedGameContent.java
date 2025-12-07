package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NonNull;

import java.util.Collection;

public interface TypedGameContent extends GameContent {
    @NonNull GameContentType<?> type();

    @NonNull Collection<@NonNull Object> content();

    boolean isEmpty();
}
