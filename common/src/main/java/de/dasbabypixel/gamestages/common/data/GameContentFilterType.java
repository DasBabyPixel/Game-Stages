package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

@NullMarked
public final class GameContentFilterType<TypeData, Elements, Element> implements TypedGameContent<TypeData, Elements, Element> {
    private final GameContent base;
    private final GameContentRegistry.Entry<?, TypeData, Elements, Element> typeEntry;
    private final TypeData typeData;

    public GameContentFilterType(GameContent base, GameContentRegistry.Entry<?, TypeData, Elements, Element> typeEntry) {
        this.base = base;
        this.typeEntry = typeEntry;
        this.typeData = typeEntry.type().newTypeData(this);
    }

    public GameContent base() {
        return base;
    }

    @Override
    public GameContentRegistry.Entry<?, TypeData, Elements, Element> typeEntry() {
        return typeEntry;
    }

    @Override
    public TypeData typeData() {
        return typeData;
    }

    @Override
    public String toString() {
        return base + ".filterType(" + typeEntry.id() + ")";
    }
}
