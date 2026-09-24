package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record GameContentOnly(GameContent base, GameContent inclusion) implements GameContent {
    @Override
    public String toString() {
        return base + ".only(" + inclusion + ")";
    }

    @Override
    public <TypeData, Elements, Element> TypedGameContent<TypeData, Elements, Element> filterType(GameContentRegistry.Entry<?, TypeData, Elements, Element> type) {
        if (base instanceof TypedGameContent<?, ?, ?> typedBase) {
            if (typedBase.typeEntry() != type) return type.empty();
            return new GameContentTypedOnly<>(typedBase.filterType(type), inclusion);
        }
        return GameContent.super.filterType(type);
    }
}
