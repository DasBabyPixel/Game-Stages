package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

@NullMarked
public sealed interface TypedGameContent<TypeData, Elements, Element> extends GameContent permits GameContentDirect, GameContentFilterType, TypedGameContentSugar {
    GameContentRegistry.Entry<?, TypeData, Elements, Element> typeEntry();

    TypeData typeData();

    @Override
    default TypedGameContent<TypeData, Elements, Element> except(GameContent... other) {
        return GameContent.super.except(other).filterType(typeEntry());
    }

    @Override
    default TypedGameContent<TypeData, Elements, Element> only(GameContent... other) {
        return GameContent.super.only(other).filterType(typeEntry());
    }

    @SuppressWarnings("unchecked")
    @Override
    default <NTypeData, NElements, NElement> TypedGameContent<NTypeData, NElements, NElement> filterType(GameContentRegistry.Entry<?, NTypeData, NElements, NElement> type) {
        if (type == typeEntry()) {
            return (TypedGameContent<NTypeData, NElements, NElement>) this;
        }
        return type.empty();
    }
}
