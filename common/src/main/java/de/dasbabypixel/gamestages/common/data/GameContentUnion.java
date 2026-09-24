package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@NullMarked
public record GameContentUnion(List<GameContent> list) implements GameContent {
    public GameContentUnion {
        list = List.copyOf(list);
    }

    public static GameContent create(Collection<? extends GameContent> c) {
        if (c.size() == 1) return c.iterator().next();
        if (c.isEmpty()) return GameContent.EMPTY;
        return new GameContentUnion(List.copyOf(c));
    }

    @Override
    public String toString() {
        return "union(" + list.stream().map(Object::toString).collect(Collectors.joining(", ")) + ")";
    }
}
