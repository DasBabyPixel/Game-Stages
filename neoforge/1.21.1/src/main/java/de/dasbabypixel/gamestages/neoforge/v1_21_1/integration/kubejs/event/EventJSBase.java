package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.BaseFunction;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Set;

public abstract class EventJSBase<Self extends EventJSBase<Self>> extends HashMap<String, BaseFunction> implements KubeEvent {
    private final EventType<Self> type;

    public EventJSBase(EventType<Self> type) {
        this.type = type;
    }

    @Override
    public @NonNull Set<String> keySet() {
        return type.functions().keySet();
    }

    @Override
    public boolean containsKey(Object key) {
        return keySet().contains(String.valueOf(key));
    }

    @Override
    public BaseFunction get(Object key) {
        var keyString = String.valueOf(key);
        return type.functions().get(keyString).invoker();
    }

    public interface Function<E extends EventJSBase<E>> {
        Object invoke(E event, KubeJSContext cx, Object[] args);
    }
}
