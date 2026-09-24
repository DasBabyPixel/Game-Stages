package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event;

import de.dasbabypixel.gamestages.common.Unit;
import de.dasbabypixel.gamestages.common.data.GameContentRegistry;
import de.dasbabypixel.gamestages.common.data.attribute.Attribute;
import de.dasbabypixel.gamestages.common.data.attribute.AttributeEntry;
import de.dasbabypixel.gamestages.common.data.attribute.SimpleImmutableAttributeHolder;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.AddonUtil;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.JSContext;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
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

    private static TypeInfo type(Object o) {
        return switch (o) {
            case Type t -> Objects.requireNonNull(TypeInfo.of(t));
            case TypeInfo t -> t;
            default -> throw new UnsupportedOperationException(String.valueOf(o));
        };
    }

    @HideFromJS
    public void addFunction(String name, EventFunction<? super Event> function, Object returnType, @Nullable Object... parameters) {
        addFunction(name, function).returnType(returnType).params(parameters).finish();
    }

    @HideFromJS
    public void addFunctionVarArgs(String name, EventFunction<? super Event> function, Object returnType, @Nullable Object... parameters) {
        addFunction(name, function).varargs().returnType(returnType).params(parameters).finish();
    }

    @HideFromJS
    public FunctionBuilder<Event> addFunction(String name, EventFunction<? super Event> function) {
        return new FunctionBuilder<>() {
            private final List<AttributeEntry<? super FunctionDescriptor<Event>, ?>> attributeEntries = new ArrayList<>();
            private Object[] parameters = new Object[0];
            private boolean varargs = false;

            @Override
            public <T> FunctionBuilder<Event> attribute(Attribute<? super FunctionDescriptor<Event>, T> attribute, T value) {
                this.attributeEntries.add(new AttributeEntry<>(attribute, value));
                return this;
            }

            @SuppressWarnings("NullableProblems")
            @Override
            public FunctionBuilder<Event> params(@Nullable Object... parameters) {
                this.parameters = parameters;
                return this;
            }

            @Override
            public FunctionBuilder<Event> varargs() {
                varargs = true;
                return this;
            }

            @Override
            public void finish() {
                var descriptor = new FunctionDescriptor<>(attributeEntries, varargs, convert(parameters));
                addFunction(name, function, descriptor);
            }
        };
    }

    @HideFromJS
    public void addFunction(String name, EventFunction<? super Event> function, FunctionDescriptor<Event> descriptor) {
        var parameters = descriptor.parameters();
        boolean varargs = descriptor.varArgs();
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

                if (varargs) {
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
        return new FunctionParameterDescriptor<>(AddonUtil.singleParameter(t), FunctionParameterConverter.defaultConverter(t));
    }

    @SuppressWarnings("unchecked")
    private List<FunctionParameterDescriptor<? super Event>> convert(@Nullable Object[] parameters) {
        var params = new ArrayList<FunctionParameterDescriptor<? super Event>>(parameters.length);
        for (var parameter : parameters) {
            params.add((FunctionParameterDescriptor<? super Event>) of(Objects.requireNonNull(parameter)));
        }
        return params;
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

    @HideFromJS
    public List<PreEventExecutor<Event>> postExecutors() {
        return postExecutors;
    }

    @HideFromJS
    public interface FunctionBuilder<Event> {
        <T> FunctionBuilder<Event> attribute(Attribute<? super FunctionDescriptor<Event>, T> attribute, T value);

        default <T> FunctionBuilder<Event> attribute(AttributeEntry<? super FunctionDescriptor<Event>, T> attributeEntry) {
            return attribute(attributeEntry.attribute(), attributeEntry.value());
        }

        default FunctionBuilder<Event> attributes(Collection<? extends AttributeEntry<? super FunctionDescriptor<Event>, ?>> attributes) {
            for (AttributeEntry<? super FunctionDescriptor<Event>, ?> attribute : attributes) {
                attribute(attribute);
            }
            return this;
        }

        FunctionBuilder<Event> params(@Nullable Object... parameters);

        default FunctionBuilder<Event> returnType(Object returnType) {
            if (returnType instanceof GameContentRegistry.Entry<?, ?, ?, ?> typeEntry) {
                return attributes(AddonUtil.returnType(typeEntry));
            }
            return attributes(AddonUtil.returnType(type(returnType)));
        }

        FunctionBuilder<Event> varargs();

        void finish();
    }

    @HideFromJS
    public interface PreEventExecutor<Event> {
        void execute(Event event);
    }

    @HideFromJS
    public interface EventFunction<Event> {
        @Nullable Object call(FunctionCall<? extends Event> functionCall, JSContext cx, Object[] args);
    }

    @HideFromJS
    @NullMarked
    public interface FunctionParameterConverter<Event> {
        static <E> FunctionParameterConverter<E> defaultConverter(TypeInfo typeInfo) {
            return (functionCall, descriptor, arg) -> Objects.requireNonNullElse(functionCall
                    .context()
                    .jsToJava(arg, typeInfo), Unit.INSTANCE);
        }

        /**
         * Converts an argument to the desired receiver type. If this is for a varargs parameter, then the {@code arg} will be an array.
         */
        Object convert(FunctionCall<? extends Event> functionCall, FunctionParameterDescriptor<? extends Event> descriptor, Object arg);
    }

    @HideFromJS
    public record FunctionCall<Event>(KubeJSContext context, JSContext cx, @Nullable Scriptable scope, Event event,
                                      Object[] args) {
    }

    @HideFromJS
    public static final class FunctionParameterDescriptor<Event> extends SimpleImmutableAttributeHolder<FunctionParameterDescriptor<Event>> {
        private final FunctionParameterConverter<? super Event> converter;

        public FunctionParameterDescriptor(Collection<? extends AttributeEntry<? super FunctionParameterDescriptor<Event>, ?>> attributes, FunctionParameterConverter<? super Event> converter) {
            super(attributes);
            this.converter = converter;
        }

        Object convert(FunctionCall<? extends Event> functionCall, Object arg) {
            return converter.convert(functionCall, this, arg);
        }
    }

    @NullMarked
    public static final class FunctionDescriptor<Event> extends SimpleImmutableAttributeHolder<FunctionDescriptor<Event>> {
        private final boolean varArgs;
        private final List<? extends FunctionParameterDescriptor<? super Event>> parameters;

        public FunctionDescriptor(Collection<? extends AttributeEntry<? super FunctionDescriptor<Event>, ?>> attributes, boolean varArgs, List<? extends FunctionParameterDescriptor<? super Event>> parameters) {
            super(attributes);
            this.varArgs = varArgs;
            this.parameters = List.copyOf(parameters);
        }

        public boolean varArgs() {
            return varArgs;
        }

        public List<? extends FunctionParameterDescriptor<? super Event>> parameters() {
            return parameters;
        }
    }

    public record Function<Event>(BaseFunction invoker, FunctionDescriptor<Event> descriptor) {
    }
}
