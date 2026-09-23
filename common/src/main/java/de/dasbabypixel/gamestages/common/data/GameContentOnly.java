package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record GameContentOnly(GameContent base, GameContent inclusion) implements GameContent {
    @Override
    public String toString() {
        return base + ".only(" + inclusion + ")";
    }
}
