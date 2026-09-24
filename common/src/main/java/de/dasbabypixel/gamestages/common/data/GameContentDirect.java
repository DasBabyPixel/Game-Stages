package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

import java.util.Collection;

@NullMarked
public final class GameContentDirect<TypeData, Elements, Element> implements TypedGameContent<TypeData, Elements, Element>, GameContentWrapper.Direct {
    private final GameContentRegistry.Entry<?, TypeData, Elements, Element> typeEntry;
    private final TypeData typeData;
    private final Elements elements;

    private GameContentDirect(GameContentRegistry.Entry<?, TypeData, Elements, Element> typeEntry, Elements elements) {
        this.typeEntry = typeEntry;
        this.elements = elements;
        this.typeData = typeEntry.type().newTypeData(this);
    }

    public static <TypeData, Elements, Element> GameContentDirect<TypeData, Elements, Element> createEmpty(GameContentRegistry.Entry<?, TypeData, Elements, Element> typeEntry) {
        return new GameContentDirect<>(typeEntry, typeEntry.type().newElementsBuilder().build());
    }

    public static <TypeData, Elements, Element> GameContentDirect<TypeData, Elements, Element> create(GameContentRegistry.Entry<?, TypeData, Elements, Element> typeEntry, Elements elements) {
        if (!typeEntry.type().iterate(elements).iterator().hasNext()) return typeEntry.empty();
        return new GameContentDirect<>(typeEntry, elements);
    }

    public GameContentSimple.TypeEntry<Elements> createTypeEntry() {
        return new GameContentSimple.TypeEntry<>(typeEntry, elements);
    }

    @Override
    public GameContentRegistry.Entry<?, TypeData, Elements, Element> typeEntry() {
        return typeEntry;
    }

    @Override
    public TypeData typeData() {
        return typeData;
    }

    public Elements elements() {
        return elements;
    }

    public boolean isEmpty() {
        return !typeEntry.type().iterate(elements).iterator().hasNext();
    }

    public Iterable<Element> content() {
        return typeEntry.type().iterate(elements);
    }

    public Collection<? extends Element> contentCollection() {
        return typeEntry.type().elementCollection(elements);
    }

    @Override
    public String toString() {
        var content = isEmpty() ? "" : typeEntry.type().toStringElements(elements);
        return "%s(%s)".formatted(typeEntry.id(), content);
    }

    @Override
    public GameContentDirect<?, ?, ?> gameContent() {
        return this;
    }
}
