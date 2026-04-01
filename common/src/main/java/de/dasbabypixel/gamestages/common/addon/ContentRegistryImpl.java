package de.dasbabypixel.gamestages.common.addon;

import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class ContentRegistryImpl implements ContentRegistry {
    private final @NonNull List<Entry<?>> entries = new ArrayList<>();

    @Override
    public <T extends TypedGameContent> @NonNull Builder<T> prepare(@NonNull GameContentType<T> type) {
        return new Builder<>(type);
    }

    public @NonNull List<@NonNull Entry<?>> entries() {
        return entries;
    }

    public class Builder<T extends TypedGameContent> implements ContentRegistry.Builder<T> {
        private final @NonNull GameContentType<T> type;
        private final @NonNull Map<Attribute, AttributeEntry<?>> attributes = new HashMap<>();

        public Builder(@NonNull GameContentType<T> type) {
            this.type = type;
        }

        @Override
        public <V> ContentRegistry.@NonNull Builder<T> set(@NonNull Attribute attribute, @NonNull V value) {
            attributes.put(attribute, new AttributeEntry<>(attribute, value));
            return this;
        }

        @Override
        public void register() {
            entries.add(new Entry<>(type, attributes));
        }
    }

    public record Entry<T extends TypedGameContent>(@NonNull GameContentType<T> type,
                                                    @NonNull Map<Attribute, AttributeEntry<?>> attributes) {
        public Entry {
            attributes = Objects.requireNonNull(Map.copyOf(attributes));
        }

        @SuppressWarnings("unchecked")
        public <V> @NonNull V attribute(Attribute attribute) {
            return (V) Objects.requireNonNull(Objects.requireNonNull(attributes.get(attribute)).value());
        }
    }

    public record AttributeEntry<T>(Attribute attribute, T value) {
    }
}
