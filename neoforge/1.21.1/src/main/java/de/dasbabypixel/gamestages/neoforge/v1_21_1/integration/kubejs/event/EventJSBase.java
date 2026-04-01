package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.BaseFunction;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public abstract class EventJSBase<Self extends EventJSBase<Self>> extends HashMap<String, BaseFunction> implements KubeEvent {
    private final @NonNull EventType<Self> type;

    public EventJSBase(@NonNull EventType<Self> type) {
        this.type = type;
    }

    @Override
    public @NonNull Set<String> keySet() {
        return type.functions().keySet();
    }

    @Override
    public boolean containsKey(@NonNull Object key) {
        return keySet().contains(String.valueOf(key));
    }

    @Override
    public BaseFunction get(@NonNull Object key) {
        var keyString = String.valueOf(key);
        return Objects.requireNonNull(type.functions().get(keyString), "Unknown event function " + keyString).invoker();
    }

    public interface Function<E extends EventJSBase<E>> {
        @Nullable Object invoke(@NonNull E event, @NonNull KubeJSContext cx, Object @NonNull [] args);
    }
}
