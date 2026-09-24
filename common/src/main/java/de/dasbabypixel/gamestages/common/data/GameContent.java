package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@NullMarked
public sealed interface GameContent extends GameContentWrapper permits GameContentExcept, GameContentMod, GameContentOnly, GameContentSimple, GameContentSugar, GameContentUnion, TypedGameContent {
    GameContent EMPTY = new GameContentSimple(List.of());

    @Override
    String toString();

    @Override
    default GameContent gameContent() {
        return this;
    }

    default GameContent except(GameContent... other) {
        if (other.length == 0) return this;
        return new GameContentExcept(this, GameContentUnion.create(Arrays.asList(other)));
    }

    default GameContent only(GameContent... other) {
        if (other.length == 0) return GameContent.EMPTY;
        return new GameContentOnly(this, GameContentUnion.create(Arrays.asList(other)));
    }

    default GameContent union(GameContent... other) {
        if (other.length == 0) return this;
        return GameContentUnion.create(Stream.concat(Stream.of(this), Arrays.stream(other)).toList());
    }

    default <TypeData, Elements, Element> TypedGameContent<TypeData, Elements, Element> filterType(GameContentRegistry.Entry<?, TypeData, Elements, Element> type) {
        return new GameContentFilterType<>(this, type);
    }
}
