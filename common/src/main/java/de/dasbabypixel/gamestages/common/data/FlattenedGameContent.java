package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class FlattenedGameContent {
    private final Map<GameContentType<?>, TypedGameContent> map;

    public FlattenedGameContent(Map<GameContentType<?>, TypedGameContent> map) {
        this.map = map;
    }

    public Set<GameContentType<?>> types() {
        return map.keySet();
    }

    @SuppressWarnings("unchecked")
    public <T extends TypedGameContent> @NonNull T get(@NonNull GameContentType<T> type) {
        return (T) Objects.requireNonNull(map.get(type));
    }
}
