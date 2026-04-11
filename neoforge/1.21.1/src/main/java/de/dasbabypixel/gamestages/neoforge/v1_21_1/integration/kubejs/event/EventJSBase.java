package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event;

import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@NullMarked
public abstract class EventJSBase<Self extends EventJSBase<Self>> extends HashMap<String, BaseFunction> implements KubeEvent {
    private final EventType<Self> type;
    private final Map<Object, @Nullable Object> extra = new HashMap<>();

    @SuppressWarnings("unchecked")
    public EventJSBase(EventType<Self> type) {
        this.type = type;
        for (var preExecutor : type.preExecutors()) {
            preExecutor.execute((Self) this);
        }
        for (var e : type.functions().values()) {
            extra.put(e.invoker(), e.contextSupplier().apply((Self) this));
        }
    }

    @Override
    public Set<String> keySet() {
        return type.functions().keySet();
    }

    @Override
    public boolean containsKey(Object key) {
        return keySet().contains(String.valueOf(key));
    }

    @Override
    public BaseFunction get(Object key) {
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
    public void afterPosted(EventResult result) {
        for (var value : type.functions().values()) {
            var context = extra.get(value.invoker());
            ((ContextFunction<Self, @NonNull Object>) value.function()).finish((Self) this, context);
        }
        for (var postExecutor : type.postExecutors()) {
            postExecutor.execute((Self) this);
        }
    }

    public interface Function<E extends EventJSBase<E>> {
        @Nullable Object invoke(E event, KubeJSContext cx, Object[] args);
    }

    public interface ContextFunction<E extends EventJSBase<E>, EventContext> {
        @Nullable Object invoke(E event, KubeJSContext cx, @Nullable EventContext eventContext, Object[] args);

        default void finish(E event, @Nullable EventContext eventContext) {
        }
    }
}
