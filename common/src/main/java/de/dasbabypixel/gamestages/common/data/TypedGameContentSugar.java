package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

@NullMarked
public non-sealed interface TypedGameContentSugar<TypeData, Elements, Element> extends TypedGameContent<TypeData, Elements, Element>, GameContentSugar {
    @Override
    TypedGameContent<TypeData, Elements, Element> desugar();
}
