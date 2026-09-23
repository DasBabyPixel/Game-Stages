package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record GameContentExcept(GameContent base, GameContent exclusion) implements GameContent {
    @Override
    public String toString() {
        return base + ".except(" + exclusion + ")";
    }
}
