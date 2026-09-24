package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

@NullMarked
public final class GameContentTypedMod<TypeData, Elements, Element> implements TypedGameContentSugar<TypeData, Elements, Element> {
    private final GameContentRegistry.Entry<?, TypeData, Elements, Element> typeEntry;
    private final TypeData typeData;
    private final String modId;

    public GameContentTypedMod(GameContentRegistry.Entry<?, TypeData, Elements, Element> typeEntry, String modId) {
        this.typeEntry = typeEntry;
        this.modId = modId;
        this.typeData = typeEntry.type().newTypeData(this);
    }

    @Override
    public GameContentRegistry.Entry<?, TypeData, Elements, Element> typeEntry() {
        return typeEntry;
    }

    @Override
    public TypeData typeData() {
        return typeData;
    }

    public String modId() {
        return modId;
    }

    @Override
    public String toString() {
        return "%s(@%s)".formatted(typeEntry.id(), modId);
    }

    @Override
    public TypedGameContent<TypeData, Elements, Element> desugar() {
        return new GameContentFilterType<>(new GameContentMod(modId), typeEntry);
    }
}
