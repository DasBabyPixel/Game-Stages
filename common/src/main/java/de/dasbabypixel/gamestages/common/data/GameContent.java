package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface GameContent {
    GameContent except(GameContent... other);

    GameContent only(GameContent... other);

    GameContent union(GameContent... other);

    GameContent filterType(GameContentType<?> type);
}
