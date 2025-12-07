package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NonNull;

public interface TypedGameContent extends GameContent {
    @NonNull GameContentType<?> type();
}
