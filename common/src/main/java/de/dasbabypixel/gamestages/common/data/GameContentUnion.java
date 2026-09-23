package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.stream.Collectors;

@NullMarked
public record GameContentUnion(List<GameContent> list) implements GameContent {
    public GameContentUnion {
        list = List.copyOf(list);
    }

    @Override
    public String toString() {
        return "union(" + list.stream().map(Object::toString).collect(Collectors.joining(", ")) + ")";
    }
}
