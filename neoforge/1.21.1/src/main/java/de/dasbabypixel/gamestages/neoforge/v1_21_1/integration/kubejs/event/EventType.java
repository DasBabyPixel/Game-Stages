package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event;

import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.ArrayTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;

@NullMarked
public final class EventType<Event extends EventJSBase<Event>> {
    private final Class<? extends Event> cls;
    private final TypeInfo type;
    private final java.util.function.Function<Event, Void> NUL = ignore -> null;
    private List<PreEventExecutor<Event>> preExecutors = new ArrayList<>();
    private List<PreEventExecutor<Event>> postExecutors = new ArrayList<>();
    private Map<String, Function<Event, ?>> functions = new HashMap<>();

    public EventType(Class<? extends Event> cls) {
        this.cls = cls;
        this.type = Objects.requireNonNull(TypeInfo.of(cls));
    }

    @HideFromJS
    public <EventContext> void addFunction(String name, java.util.function.Function<Event, EventContext> contextSupplier, EventJSBase.ContextFunction<Event, EventContext> function, Object returnType, Object... parameters) {
        var retType = of(returnType);
        var params = new ExplicitType[parameters.length];
        for (var i = 0; i < parameters.length; i++) {
            params[i] = of(parameters[i]);
        }
        addFunction(name, contextSupplier, function, false, null, retType, params);
    }

    @HideFromJS
    public void addFunction(String name, EventJSBase.Function<Event> function, Object returnType, Object... parameters) {
        addFunction(name, NUL, wrap(function), returnType, parameters);
    }

    @HideFromJS
    public <EventContext> void addFunctionVarArgs(String name, java.util.function.Function<Event, EventContext> contextSupplier, EventJSBase.ContextFunction<Event, EventContext> function, @Nullable Object wrapVarargsType, Object returnType, Object... parameters) {
        var retType = of(returnType);
        var params = new ExplicitType[parameters.length];
        for (var i = 0; i < parameters.length; i++) {
            params[i] = of(parameters[i]);
        }
        addFunction(name, contextSupplier, function, true, type(wrapVarargsType), retType, params);
    }

    @HideFromJS
    public void addFunctionVarArgs(String name, EventJSBase.Function<Event> function, @Nullable Object wrapVarargsType, Object returnType, Object... parameters) {
        addFunctionVarArgs(name, NUL, wrap(function), wrapVarargsType, returnType, parameters);
    }

    private ExplicitType of(Object o) {
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
    public <EventContext> void addFunction(String name, java.util.function.Function<Event, EventContext> contextSupplier, EventJSBase.ContextFunction<Event, EventContext> function, boolean varArgs, @Nullable TypeInfo wrapVarargsType, ExplicitType returnType, ExplicitType... parameters) {
        addFunction(name, contextSupplier, function, new FunctionDescriptor(varArgs, returnType, parameters), wrapVarargsType);
    }

    @HideFromJS
    public void addFunction(String name, EventJSBase.Function<Event> function, boolean varArgs, @Nullable TypeInfo wrapVarargsType, ExplicitType returnType, ExplicitType... parameters) {
        addFunction(name, NUL, wrap(function), varArgs, wrapVarargsType, returnType, parameters);
    }

    @HideFromJS
    public void addFunction(String name, EventJSBase.Function<Event> function, FunctionDescriptor descriptor, @Nullable TypeInfo wrapVarargsType) {
        addFunction(name, NUL, wrap(function), descriptor, wrapVarargsType);
    }

    @HideFromJS
    public <EventContext> void addFunction(String name, java.util.function.Function<Event, EventContext> contextSupplier, EventJSBase.ContextFunction<Event, EventContext> function, FunctionDescriptor descriptor, @Nullable TypeInfo wrapVarargsType) {
        var parameters = descriptor.parameters();
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
            public @Nullable Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
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
                        args[i] = Objects.requireNonNull(cx.jsToJava(args[i], parameters[i].jsType()));
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
    public Map<String, Function<Event, ?>> functions() {
        return functions;
    }

    @HideFromJS
    public List<PreEventExecutor<Event>> preExecutors() {
        return preExecutors;
    }

    public List<PreEventExecutor<Event>> postExecutors() {
        return postExecutors;
    }

    private static <E extends EventJSBase<E>> EventJSBase.ContextFunction<E, Void> wrap(EventJSBase.Function<E> function) {
        return (event, cx, unused, args) -> function.invoke(event, cx, args);
    }

    public interface PreEventExecutor<Event extends EventJSBase<Event>> {
        void execute(Event event);
    }

    public interface PostEventExecutor<Event extends EventJSBase<Event>> {
        void execute(Event event);
    }

    public record ExplicitType(TypeInfo jsType, TypeInfo probeType) {
    }

    public record FunctionDescriptor(boolean varArgs, ExplicitType returnType, ExplicitType[] parameters) {
    }

    public record Function<Event extends EventJSBase<Event>, EventContext>(BaseFunction invoker,
                                                                           java.util.function.Function<? super Event, EventContext> contextSupplier,
                                                                           EventJSBase.ContextFunction<? super Event, EventContext> function,
                                                                           FunctionDescriptor descriptor) {
    }
}
