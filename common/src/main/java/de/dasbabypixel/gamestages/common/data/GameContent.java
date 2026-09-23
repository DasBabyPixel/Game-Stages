package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@NullMarked
public sealed interface GameContent extends GameContentWrapper permits GameContentExcept, GameContentMod, GameContentOnly, GameContentSimple, GameContentUnion, TypedGameContent {
    GameContent EMPTY = new GameContentSimple(List.of());

    @Override
    default GameContent gameContent() {
        return this;
    }

    default GameContent except(GameContent... other) {
        return new GameContentExcept(this, new GameContentUnion(Arrays.asList(other)));
    }

    default GameContent only(GameContent... other) {
        return new GameContentOnly(this, new GameContentUnion(Arrays.asList(other)));
    }

    default GameContent union(GameContent... other) {
        return new GameContentUnion(Stream.concat(Stream.of(this), Arrays.stream(other)).toList());
    }

    default <TypeData, Elements, Element> TypedGameContent<TypeData, Elements, Element> filterType(GameContentRegistry.Entry<?, TypeData, Elements, Element> type) {
        return new GameContentFilterType<>(this, type);
    }
}
