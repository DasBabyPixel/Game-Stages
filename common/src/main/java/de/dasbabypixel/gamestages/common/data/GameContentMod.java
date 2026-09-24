package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record GameContentMod(String modId) implements GameContent {
    @Override
    public String toString() {
        return "@" + modId;
    }

    @Override
    public <TypeData, Elements, Element> TypedGameContent<TypeData, Elements, Element> filterType(GameContentRegistry.Entry<?, TypeData, Elements, Element> type) {
        return new GameContentTypedMod<>(type, modId);
    }
}
