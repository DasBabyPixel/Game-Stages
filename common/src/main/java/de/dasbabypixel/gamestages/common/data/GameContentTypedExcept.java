package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

@NullMarked
public final class GameContentTypedExcept<TypeData, Elements, Element> implements TypedGameContentSugar<TypeData, Elements, Element> {
    private final TypedGameContent<TypeData, Elements, Element> base;
    private final GameContent exclusion;
    private final TypeData typeData;

    public GameContentTypedExcept(TypedGameContent<TypeData, Elements, Element> base, GameContent exclusion) {
        this.base = base;
        this.exclusion = exclusion;
        this.typeData = base.typeEntry().type().newTypeData(this);
    }

    @Override
    public GameContentRegistry.Entry<?, TypeData, Elements, Element> typeEntry() {
        return base.typeEntry();
    }

    @Override
    public TypeData typeData() {
        return typeData;
    }

    @Override
    public TypedGameContent<TypeData, Elements, Element> desugar() {
        return new GameContentFilterType<>(new GameContentExcept(base, exclusion), base.typeEntry());
    }

    @Override
    public String toString() {
        return "%s.except(%s)".formatted(base, exclusion);
    }
}
