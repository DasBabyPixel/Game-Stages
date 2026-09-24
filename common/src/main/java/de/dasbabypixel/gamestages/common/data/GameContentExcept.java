package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record GameContentExcept(GameContent base, GameContent exclusion) implements GameContent {
    @Override
    public String toString() {
        return base + ".except(" + exclusion + ")";
    }

    @Override
    public <TypeData, Elements, Element> TypedGameContent<TypeData, Elements, Element> filterType(GameContentRegistry.Entry<?, TypeData, Elements, Element> type) {
        if (base instanceof TypedGameContent<?, ?, ?> typedBase) {
            if (typedBase.typeEntry() != type) return type.empty();
            return new GameContentTypedExcept<>(typedBase.filterType(type), exclusion);
        }
        return GameContent.super.filterType(type);
    }
}
