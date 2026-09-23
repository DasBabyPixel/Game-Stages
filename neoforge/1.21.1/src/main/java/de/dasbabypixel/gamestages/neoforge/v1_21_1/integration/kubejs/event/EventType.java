package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event;

import de.dasbabypixel.gamestages.common.Unit;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.JSContext;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.ArrayTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public final class EventType<Event extends EventJSBase<? extends Event>> {
    private final Class<? extends Event> cls;
    private final TypeInfo type;
    private List<PreEventExecutor<Event>> preExecutors = new ArrayList<>();
    private List<PreEventExecutor<Event>> postExecutors = new ArrayList<>();
    private Map<String, Function<Event>> functions = new HashMap<>();

    public EventType(Class<? extends Event> cls) {
        this.cls = cls;
        this.type = Objects.requireNonNull(TypeInfo.of(cls));
    }

    @HideFromJS
    public void addFunction(String name, EventFunction<? super Event> function, Object returnType, @Nullable Object... parameters) {
        addFunction(name, function, type(returnType), convert(parameters));
    }

    @HideFromJS
    public void addFunction(String name, EventFunction<? super Event> function, TypeInfo returnType, List<? extends FunctionParameterDescriptor<? super Event>> parameters) {
        addFunction(name, function, new FunctionDescriptor<>(false, returnType, parameters));
    }

    @HideFromJS
    public void addFunctionVarArgs(String name, EventFunction<? super Event> function, Object returnType, @Nullable Object... parameters) {
        addFunctionVarArgs(name, function, type(returnType), convert(parameters));
    }

    @HideFromJS
    public void addFunctionVarArgs(String name, EventFunction<? super Event> function, TypeInfo returnType, List<? extends FunctionParameterDescriptor<? super Event>> parameters) {
        addFunction(name, function, new FunctionDescriptor<>(true, returnType, parameters));
    }

    @HideFromJS
    public void addFunction(String name, EventFunction<? super Event> function, FunctionDescriptor<Event> descriptor) {
        var parameters = descriptor.parameters();
        ArrayTypeInfo varargs;
        if (descriptor.varArgs()) {
            if (!(descriptor.parameters().getLast().typeInfo() instanceof ArrayTypeInfo array)) {
                throw new UnsupportedOperationException("Varargs function must expect array as varargs parameter");
            }
            varargs = array;
        } else varargs = null;
        var invoker = new BaseFunction() {
            @SuppressWarnings("DataFlowIssue")
            @NullUnmarked
            @Override
            public @Nullable Object call(Context cx_, Scriptable scope, Scriptable thisObj, Object[] args) {
                Objects.requireNonNull(cx_);
                Objects.requireNonNull(args);
                Objects.requireNonNull(thisObj);
                var cx = (KubeJSContext) cx_;
                var event = cls.cast(Objects.requireNonNull(cx.jsToJava(thisObj, type)));

                var functionCall = new FunctionCall<Event>(cx, JSContext.instance(cx), scope, event, args);

                var newArgs = new Object[parameters.size()];

                if (varargs != null) {
                    if (args.length < parameters.size() - 1) {
                        throw new IllegalArgumentException("Too few arguments. Need at least " + (parameters.size() - 1));
                    }
                    for (var i = 0; i < parameters.size() - 1; i++) {
                        var jsArgument = Objects.requireNonNullElse(args[i], Unit.INSTANCE);
                        newArgs[i] = parameters.get(i).convert(functionCall, jsArgument);
                    }
                    var varargsArray = new Object[args.length - parameters.size() + 1];
                    for (int i = parameters.size() - 1, j = 0; i < args.length; i++, j++) {
                        Array.set(varargsArray, j, args[i]);
                    }
                    newArgs[parameters.size() - 1] = parameters.getLast().convert(functionCall, varargsArray);
                } else {
                    if (args.length != parameters.size()) {
                        throw new IllegalArgumentException("Wrong number of arguments. Need exactly " + parameters.size());
                    }
                    for (var i = 0; i < args.length; i++) {
                        newArgs[i] = parameters
                                .get(i)
                                .convert(functionCall, Objects.requireNonNullElse(args[i], Unit.INSTANCE));
                    }
                }
                return function.call(functionCall, functionCall.cx, newArgs);
            }
        };
        var wrappedFunction = new Function<Event>(invoker, descriptor);
        functions.put(name, wrappedFunction);
    }

    private FunctionParameterDescriptor<?> of(Object o) {
        if (o instanceof EventType.FunctionParameterDescriptor<?> t) return t;
        var t = Objects.requireNonNull(type(o));
        return new FunctionParameterDescriptor<>(t, FunctionParameterConverter.defaultConverter());
    }

    @SuppressWarnings("unchecked")
    private List<FunctionParameterDescriptor<? super Event>> convert(@Nullable Object[] parameters) {
        var params = new ArrayList<FunctionParameterDescriptor<? super Event>>(parameters.length);
        for (var parameter : parameters) {
            params.add((FunctionParameterDescriptor<? super Event>) of(Objects.requireNonNull(parameter)));
        }
        return params;
    }

    private TypeInfo type(Object o) {
        return switch (o) {
            case Type t -> Objects.requireNonNull(TypeInfo.of(t));
            case TypeInfo t -> t;
            default -> throw new UnsupportedOperationException(String.valueOf(o));
        };
    }

    @HideFromJS
    public void freeze() {
        functions = Objects.requireNonNull(Map.copyOf(functions));
        preExecutors = Objects.requireNonNull(List.copyOf(preExecutors));
        postExecutors = Objects.requireNonNull(List.copyOf(postExecutors));
    }

    @HideFromJS
    public Map<String, Function<Event>> functions() {
        return functions;
    }

    @HideFromJS
    public List<PreEventExecutor<Event>> preExecutors() {
        return preExecutors;
    }

    public List<PreEventExecutor<Event>> postExecutors() {
        return postExecutors;
    }

    public interface PreEventExecutor<Event> {
        void execute(Event event);
    }

    public interface EventFunction<Event> {
        @Nullable Object call(FunctionCall<? extends Event> functionCall, JSContext cx, Object[] args);
    }

    @NullMarked
    public interface FunctionParameterConverter<Event> {
        static <E> FunctionParameterConverter<E> defaultConverter() {
            return (functionCall, descriptor, arg) -> Objects.requireNonNullElse(functionCall
                    .context()
                    .jsToJava(arg, descriptor.typeInfo()), Unit.INSTANCE);
        }

        /**
         * Converts an argument to the desired receiver type. If this is for a varargs parameter, then the {@code arg} will be an array.
         */
        Object convert(FunctionCall<? extends Event> functionCall, FunctionParameterDescriptor<? extends Event> descriptor, Object arg);
    }

    public record FunctionCall<Event>(KubeJSContext context, JSContext cx, @Nullable Scriptable scope, Event event,
                                      Object[] args) {
    }

    public record FunctionParameterDescriptor<Event>(TypeInfo typeInfo,
                                                     FunctionParameterConverter<? super Event> converter) {
        Object convert(FunctionCall<? extends Event> functionCall, Object arg) {
            return converter.convert(functionCall, this, arg);
        }
    }

    @NullMarked
    public record FunctionDescriptor<Event>(boolean varArgs, TypeInfo returnType,
                                            List<? extends FunctionParameterDescriptor<? super Event>> parameters) {
        public FunctionDescriptor {
            parameters = List.copyOf(parameters);
        }
    }

    public record Function<Event>(BaseFunction invoker, FunctionDescriptor<Event> descriptor) {
    }
}
