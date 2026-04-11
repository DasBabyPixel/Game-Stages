package de.dasbabypixel.gamestages.neoforge.v1_21_1.addon;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventJSBase;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventType;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class EventRegistryImpl implements EventRegistry {
    private final Map<Class<?>, EventType<?>> types = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <Event extends EventJSBase<Event>> EventType<Event> get(Class<Event> cls) {
        return (EventType<Event>) Objects.requireNonNull(types.get(cls));
    }

    public <E extends EventJSBase<E>> void add(Class<E> cls, EventType<E> type) {
        types.put(cls, type);
    }

    public Map<Class<?>, EventType<?>> types() {
        return types;
    }

    public void freeze() {
        for (var value : types.values()) {
            value.freeze();
        }
    }
}
