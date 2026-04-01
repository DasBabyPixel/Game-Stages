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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class EventType<Event extends EventJSBase<Event>> {
    private final @NonNull Class<? extends Event> cls;
    private final @NonNull TypeInfo type;
    private @NonNull Map<String, Function<Event>> functions = new HashMap<>();

    public EventType(@NonNull Class<? extends Event> cls) {
        this.cls = cls;
        this.type = Objects.requireNonNull(TypeInfo.of(cls));
    }

    @HideFromJS
    public void addFunction(@NonNull String name, EventJSBase.@NonNull Function<? super Event> function, @NonNull Object returnType, @NonNull Object @NonNull ... parameters) {
        var retType = of(returnType);
        var params = new ExplicitType[parameters.length];
        for (var i = 0; i < parameters.length; i++) {
            params[i] = of(parameters[i]);
        }
        addFunction(name, function, false, null, retType, params);
    }

    @HideFromJS
    public void addFunctionVarArgs(@NonNull String name, EventJSBase.@NonNull Function<? super Event> function, @Nullable Object wrapVarargsType, @NonNull Object returnType, @NonNull Object @NonNull ... parameters) {
        var retType = of(returnType);
        var params = new ExplicitType[parameters.length];
        for (var i = 0; i < parameters.length; i++) {
            params[i] = of(parameters[i]);
        }
        addFunction(name, function, true, type(wrapVarargsType), retType, params);
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
    public void addFunction(@NonNull String name, EventJSBase.@NonNull Function<? super Event> function, boolean varArgs, @Nullable TypeInfo wrapVarargsType, @NonNull ExplicitType returnType, @NonNull ExplicitType @NonNull ... parameters) {
        addFunction(name, function, new FunctionDescriptor(varArgs, returnType, parameters), wrapVarargsType);
    }

    @HideFromJS
    public void addFunction(@NonNull String name, EventJSBase.@NonNull Function<? super Event> function, @NonNull FunctionDescriptor descriptor, @Nullable TypeInfo wrapVarargsType) {
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
                return function.invoke(Objects.requireNonNull(cls.cast(cx.jsToJava(thisObj, type))), (KubeJSContext) cx, args);
            }
        };
        var wrappedFunction = new Function<Event>(invoker, function, descriptor);
        functions.put(name, wrappedFunction);
    }

    @HideFromJS
    public void freeze() {
        functions = Objects.requireNonNull(Map.copyOf(functions));
    }

    @HideFromJS
    public Map<String, Function<Event>> functions() {
        return functions;
    }

    public record ExplicitType(@NonNull TypeInfo jsType, @NonNull TypeInfo probeType) {
    }

    public record FunctionDescriptor(boolean varArgs, @NonNull ExplicitType returnType,
                                     @NonNull ExplicitType @NonNull [] parameters) {
    }

    public record Function<Event extends EventJSBase<Event>>(@NonNull BaseFunction invoker,
                                                             EventJSBase.@NonNull Function<? super Event> function,
                                                             @NonNull FunctionDescriptor descriptor) {
    }
}
