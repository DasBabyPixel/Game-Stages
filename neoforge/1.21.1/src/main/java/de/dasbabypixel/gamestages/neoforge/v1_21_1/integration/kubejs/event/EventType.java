package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event;

import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.ArrayTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;

public final class EventType<Event extends EventJSBase<Event>> {
    private final @NonNull Class<? extends Event> cls;
    private final @NonNull TypeInfo type;
    private final java.util.function.@NonNull Function<Event, Void> NUL = ignore -> null;
    private @NonNull List<PreEventExecutor<Event>> preExecutors = new ArrayList<>();
    private @NonNull List<PreEventExecutor<Event>> postExecutors = new ArrayList<>();
    private @NonNull Map<@NonNull String, @NonNull Function<Event, ?>> functions = new HashMap<>();

    public EventType(@NonNull Class<? extends Event> cls) {
        this.cls = cls;
        this.type = Objects.requireNonNull(TypeInfo.of(cls));
    }

    @HideFromJS
    public <EventContext> void addFunction(@NonNull String name, java.util.function.@NonNull Function<@NonNull Event, EventContext> contextSupplier, EventJSBase.@NonNull ContextFunction<@NonNull Event, EventContext> function, @NonNull Object returnType, @NonNull Object @NonNull ... parameters) {
        var retType = of(returnType);
        var params = new ExplicitType[parameters.length];
        for (var i = 0; i < parameters.length; i++) {
            params[i] = of(parameters[i]);
        }
        addFunction(name, contextSupplier, function, false, null, retType, params);
    }

    @HideFromJS
    public void addFunction(@NonNull String name, EventJSBase.@NonNull Function<Event> function, @NonNull Object returnType, @NonNull Object @NonNull ... parameters) {
        addFunction(name, NUL, wrap(function), returnType, parameters);
    }

    @HideFromJS
    public <EventContext> void addFunctionVarArgs(@NonNull String name, java.util.function.@NonNull Function<@NonNull Event, EventContext> contextSupplier, EventJSBase.@NonNull ContextFunction<@NonNull Event, EventContext> function, @Nullable Object wrapVarargsType, @NonNull Object returnType, @NonNull Object @NonNull ... parameters) {
        var retType = of(returnType);
        var params = new ExplicitType[parameters.length];
        for (var i = 0; i < parameters.length; i++) {
            params[i] = of(parameters[i]);
        }
        addFunction(name, contextSupplier, function, true, type(wrapVarargsType), retType, params);
    }

    @HideFromJS
    public void addFunctionVarArgs(@NonNull String name, EventJSBase.@NonNull Function<Event> function, @Nullable Object wrapVarargsType, @NonNull Object returnType, @NonNull Object @NonNull ... parameters) {
        addFunctionVarArgs(name, NUL, wrap(function), wrapVarargsType, returnType, parameters);
    }

    private @NonNull ExplicitType of(@NonNull Object o) {
        if (o instanceof ExplicitType t) return t;
        var t = Objects.requireNonNull(type(o));
        return new ExplicitType(t, t);
    }

    private @Nullable TypeInfo type(@Nullable Object o) {
        return switch (o) {
            case null -> null;
            case Type t -> TypeInfo.of(t);
            case TypeInfo t -> t;
            default -> throw new UnsupportedOperationException(String.valueOf(o));
        };
    }

    @HideFromJS
    public <EventContext> void addFunction(@NonNull String name, java.util.function.@NonNull Function<@NonNull Event, EventContext> contextSupplier, EventJSBase.@NonNull ContextFunction<Event, EventContext> function, boolean varArgs, @Nullable TypeInfo wrapVarargsType, @NonNull ExplicitType returnType, @NonNull ExplicitType @NonNull ... parameters) {
        addFunction(name, contextSupplier, function, new FunctionDescriptor(varArgs, returnType, parameters), wrapVarargsType);
    }

    @HideFromJS
    public void addFunction(@NonNull String name, EventJSBase.@NonNull Function<Event> function, boolean varArgs, @Nullable TypeInfo wrapVarargsType, @NonNull ExplicitType returnType, @NonNull ExplicitType @NonNull ... parameters) {
        addFunction(name, NUL, wrap(function), varArgs, wrapVarargsType, returnType, parameters);
    }

    @HideFromJS
    public void addFunction(@NonNull String name, EventJSBase.@NonNull Function<Event> function, @NonNull FunctionDescriptor descriptor, @Nullable TypeInfo wrapVarargsType) {
        addFunction(name, NUL, wrap(function), descriptor, wrapVarargsType);
    }

    @HideFromJS
    public <EventContext> void addFunction(@NonNull String name, java.util.function.@NonNull Function<@NonNull Event, EventContext> contextSupplier, EventJSBase.@NonNull ContextFunction<Event, EventContext> function, @NonNull FunctionDescriptor descriptor, @Nullable TypeInfo wrapVarargsType) {
        @NonNull ExplicitType[] parameters = descriptor.parameters();
        var varArgs = descriptor.varArgs();
        ArrayTypeInfo varArgArrayType;
        if (varArgs) {
            if (!(descriptor.parameters()[descriptor.parameters().length - 1].jsType() instanceof ArrayTypeInfo array)) {
                throw new UnsupportedOperationException("Varargs function not expecting array");
            }
            varArgArrayType = array;
        } else varArgArrayType = null;
        var invoker = new BaseFunction() {
            @SuppressWarnings("unchecked")
            @Override
            public Object call(@NonNull Context cx, @NonNull Scriptable scope, Scriptable thisObj, Object @NonNull [] args) {
                if (varArgs) {
                    var newArgs = new Object[parameters.length];
                    for (var i = 0; i < parameters.length - 1; i++) {
                        newArgs[i] = cx.jsToJava(args[i], parameters[i].jsType());
                    }
                    var mergedLength = args.length - parameters.length + 1;
                    var merged = Objects.requireNonNull(varArgArrayType.componentType()).newArray(mergedLength);
                    for (int i = parameters.length - 1, j = 0; i < args.length; i++, j++) {
                        Array.set(merged, j, cx.jsToJava(args[i], varArgArrayType.componentType()));
                    }
                    if (wrapVarargsType != null) {
                        merged = cx.jsToJava(merged, wrapVarargsType);
                    }
                    newArgs[newArgs.length - 1] = merged;
                    args = newArgs;
                } else {
                    for (var i = 0; i < args.length; i++) {
                        args[i] = cx.jsToJava(args[i], parameters[i].jsType());
                    }
                }
                var event = Objects.requireNonNull(cls.cast(cx.jsToJava(thisObj, type)));
                var eventContext = (EventContext) event.extra().get(this);
                return function.invoke(event, (KubeJSContext) cx, eventContext, args);
            }
        };
        var wrappedFunction = new Function<Event, EventContext>(invoker, contextSupplier, function, descriptor);
        functions.put(name, wrappedFunction);
    }

    @HideFromJS
    public void freeze() {
        functions = Map.copyOf(functions);
        preExecutors = List.copyOf(preExecutors);
        postExecutors = List.copyOf(postExecutors);
    }

    @HideFromJS
    public @NonNull Map<@NonNull String, @NonNull Function<Event, ?>> functions() {
        return functions;
    }

    @HideFromJS
    public @NonNull List<@NonNull PreEventExecutor<Event>> preExecutors() {
        return preExecutors;
    }

    public @NonNull List<@NonNull PreEventExecutor<Event>> postExecutors() {
        return postExecutors;
    }

    private static <E extends EventJSBase<E>> EventJSBase.@NonNull ContextFunction<E, Void> wrap(EventJSBase.@NonNull Function<E> function) {
        return (event, cx, unused, args) -> function.invoke(event, cx, args);
    }

    public interface PreEventExecutor<Event extends EventJSBase<Event>> {
        void execute(Event event);
    }

    public interface PostEventExecutor<Event extends EventJSBase<Event>> {
        void execute(Event event);
    }

    public record ExplicitType(@NonNull TypeInfo jsType, @NonNull TypeInfo probeType) {
    }

    public record FunctionDescriptor(boolean varArgs, @NonNull ExplicitType returnType,
                                     @NonNull ExplicitType @NonNull [] parameters) {
    }

    public record Function<Event extends EventJSBase<Event>, EventContext>(@NonNull BaseFunction invoker,
                                                                           java.util.function.@NonNull Function<? super @NonNull Event, EventContext> contextSupplier,
                                                                           EventJSBase.@NonNull ContextFunction<? super Event, EventContext> function,
                                                                           @NonNull FunctionDescriptor descriptor) {
    }
}
