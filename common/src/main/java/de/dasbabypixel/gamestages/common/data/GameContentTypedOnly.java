package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

@NullMarked
public final class GameContentTypedOnly<TypeData, Elements, Element> implements TypedGameContentSugar<TypeData, Elements, Element> {
    private final TypedGameContent<TypeData, Elements, Element> base;
    private final GameContent inclusion;
    private final TypeData typeData;

    public GameContentTypedOnly(TypedGameContent<TypeData, Elements, Element> base, GameContent inclusion) {
        this.base = base;
        this.inclusion = inclusion;
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
        return new GameContentFilterType<>(new GameContentOnly(base, inclusion), base.typeEntry());
    }

    @Override
    public String toString() {
        return "%s.only(%s)".formatted(base, inclusion);
    }
}
