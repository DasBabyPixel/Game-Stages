package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event;

import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.ArrayTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class EventType<Event extends EventJSBase<Event>> {
    private final Class<? extends Event> cls;
    private final TypeInfo type;
    private Map<String, Function<Event>> functions = new HashMap<>();

    public EventType(Class<? extends Event> cls) {
        this.cls = cls;
        this.type = TypeInfo.of(cls);
    }

    @HideFromJS
    public void addFunction(String name, EventJSBase.Function<? super Event> function, Object returnType, Object... parameters) {
        var retType = of(returnType);
        var params = new ExplicitType[parameters.length];
        for (var i = 0; i < parameters.length; i++) {
            params[i] = of(parameters[i]);
        }
        addFunction(name, function, false, null, retType, params);
    }

    @HideFromJS
    public void addFunctionVarArgs(String name, EventJSBase.Function<? super Event> function, @Nullable Object wrapVarargsType, Object returnType, Object... parameters) {
        var retType = of(returnType);
        var params = new ExplicitType[parameters.length];
        for (var i = 0; i < parameters.length; i++) {
            params[i] = of(parameters[i]);
        }
        addFunction(name, function, true, type(wrapVarargsType), retType, params);
    }

    private ExplicitType of(Object o) {
        if (o instanceof ExplicitType t) return t;
        var t = type(o);
        return new ExplicitType(t, t);
    }

    private TypeInfo type(Object o) {
        return switch (o) {
            case null -> null;
            case Type t -> TypeInfo.of(t);
            case TypeInfo t -> t;
            default -> throw new UnsupportedOperationException(String.valueOf(o));
        };
    }

    @HideFromJS
    public void addFunction(String name, EventJSBase.Function<? super Event> function, boolean varArgs, @Nullable TypeInfo wrapVarargsType, ExplicitType returnType, ExplicitType... parameters) {
        addFunction(name, function, new FunctionDescriptor(varArgs, returnType, parameters), wrapVarargsType);
    }

    @HideFromJS
    public void addFunction(String name, EventJSBase.Function<? super Event> function, FunctionDescriptor descriptor, @Nullable TypeInfo wrapVarargsType) {
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
            @Override
            public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                if (varArgs) {
                    var newArgs = new Object[parameters.length];
                    for (var i = 0; i < parameters.length - 1; i++) {
                        newArgs[i] = cx.jsToJava(args[i], parameters[i].jsType());
                    }
                    var mergedLength = args.length - parameters.length + 1;
                    var merged = varArgArrayType.componentType().newArray(mergedLength);
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
                return function.invoke(cls.cast(cx.jsToJava(thisObj, type)), (KubeJSContext) cx, args);
            }
        };
        var wrappedFunction = new Function<Event>(invoker, function, descriptor);
        functions.put(name, wrappedFunction);
    }

    @HideFromJS
    public void freeze() {
        functions = Map.copyOf(functions);
    }

    @HideFromJS
    public Map<String, Function<Event>> functions() {
        return functions;
    }

    public record ExplicitType(TypeInfo jsType, TypeInfo probeType) {
    }

    public record FunctionDescriptor(boolean varArgs, ExplicitType returnType, ExplicitType[] parameters) {
    }

    public record Function<Event extends EventJSBase<Event>>(BaseFunction invoker,
                                                             EventJSBase.Function<? super Event> function,
                                                             FunctionDescriptor descriptor) {
    }
}
