package de.dasbabypixel.gamestages.common.addon;

import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentRegistryImpl implements ContentRegistry {
    private final List<Entry<?>> entries = new ArrayList<>();

    @Override
    public <T extends TypedGameContent> Builder<T> prepare(GameContentType<T> type) {
        return new Builder<>(type);
    }

    public List<Entry<?>> entries() {
        return entries;
    }

    public record Entry<T extends TypedGameContent>(GameContentType<T> type,
                                                    Map<Attribute<?>, AttributeEntry<?>> attributes) {
        public Entry {
            attributes = Map.copyOf(attributes);
        }

        @SuppressWarnings("unchecked")
        public <V> V attribute(Attribute<V> attribute) {
            return (V) attributes.get(attribute).value();
        }
    }

    public record AttributeEntry<T>(Attribute<T> attribute, T value) {
    }

    public class Builder<T extends TypedGameContent> implements ContentRegistry.Builder<T> {
        private final GameContentType<T> type;
        private final Map<Attribute<?>, AttributeEntry<?>> attributes = new HashMap<>();

        public Builder(GameContentType<T> type) {
            this.type = type;
        }

        @Override
        public <V> ContentRegistry.Builder<T> set(Attribute<V> attribute, V value) {
            attributes.put(attribute, new AttributeEntry<>(attribute, value));
            return this;
        }

        @Override
        public void register() {
            entries.add(new Entry<>(type, attributes));
        }
    }
}
