package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface GameContentWrapper {
    GameContent gameContent();

    interface Direct extends GameContentWrapper {
        GameContentDirect<?, ?, ?> gameContent();
    }
}
