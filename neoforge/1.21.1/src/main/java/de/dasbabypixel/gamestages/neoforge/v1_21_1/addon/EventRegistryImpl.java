package de.dasbabypixel.gamestages.neoforge.v1_21_1.addon;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventJSBase;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventType;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EventRegistryImpl implements EventRegistry {
    private final @NonNull Map<@NonNull Class<?>, @NonNull EventType<?>> types = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <Event extends EventJSBase<Event>> @NonNull EventType<Event> get(Class<Event> cls) {
        return (EventType<Event>) Objects.requireNonNull(types.get(cls));
    }

    public <E extends EventJSBase<E>> void add(@NonNull Class<E> cls, @NonNull EventType<E> type) {
        types.put(cls, type);
    }

    public @NonNull Map<Class<?>, EventType<?>> types() {
        return types;
    }

    public void freeze() {
        for (var value : types.values()) {
            value.freeze();
        }
    }
}
