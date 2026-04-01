package de.dasbabypixel.gamestages.common.data.flattening;

import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class FlattenedGameContent {
    private final @NonNull Map<@NonNull GameContentType<?>, TypedGameContent> map;

    public FlattenedGameContent(@NonNull Map<@NonNull GameContentType<?>, TypedGameContent> map) {
        this.map = map;
    }

    public @NonNull Set<@NonNull GameContentType<?>> types() {
        return map.keySet();
    }

    @SuppressWarnings("unchecked")
    public <T extends TypedGameContent> @NonNull T get(@NonNull GameContentType<T> type) {
        return (T) Objects.requireNonNull(map.get(type));
    }
}
