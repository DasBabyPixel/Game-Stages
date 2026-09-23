package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event;

import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@NullMarked
public abstract class EventJSBase<Self extends EventJSBase<? extends Self>> extends HashMap<String, BaseFunction> implements KubeEvent {
    private final EventType<Self> type;
    private final Map<Object, @Nullable Object> extra = new HashMap<>();

    public EventJSBase(EventType<Self> type) {
        this.type = type;
        for (var preExecutor : type.preExecutors()) {
            preExecutor.execute(self());
        }
        for (var e : type.functions().values()) {
            // TODO init contexts
        }
    }

    @SuppressWarnings("unchecked")
    private Self self() {
        return (Self) this;
    }

    @Override
    public Set<String> keySet() {
        return type.functions().keySet();
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        Objects.requireNonNull(key);
        return keySet().contains(String.valueOf(key));
    }

    @Override
    public BaseFunction get(@Nullable Object key) {
        Objects.requireNonNull(key);
        var keyString = String.valueOf(key);
        return Objects.requireNonNull(type.functions().get(keyString), "Unknown event function " + keyString).invoker();
    }

    @HideFromJS
    public Map<Object, @Nullable Object> extra() {
        return extra;
    }

    @SuppressWarnings("unchecked")
    @Override
    @HideFromJS
    public void afterPosted(@Nullable EventResult result) {
        for (var value : type.functions().values()) {
            var context = extra.get(value.invoker());
            // TODO invoke afterPosted callback for contexts
        }
        for (var postExecutor : type.postExecutors()) {
            postExecutor.execute(self());
        }
    }
}
