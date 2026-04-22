package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public interface GameContentType<T extends TypedGameContent> {
    List<GameContentType<?>> TYPES = new ArrayList<>();
}
