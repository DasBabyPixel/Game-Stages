package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record GameContentMod(String modId) implements GameContent {
    @Override
    public String toString() {
        return "@" + modId;
    }
}
