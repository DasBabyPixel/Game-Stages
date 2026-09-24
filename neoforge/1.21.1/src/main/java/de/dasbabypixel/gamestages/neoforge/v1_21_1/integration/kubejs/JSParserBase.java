package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.GameContentDirect;
import de.dasbabypixel.gamestages.common.data.GameContentRegistry;
import de.dasbabypixel.gamestages.common.data.GameContentTypedMod;
import de.dasbabypixel.gamestages.common.data.GameContentUnion;
import de.dasbabypixel.gamestages.common.data.GameContentWrapper;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.AddonUtil;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventType;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.GameCollectionJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.GameCollectionJSImpl;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

@NullMarked
public class JSParserBase {
    private final Map<Class<?>, Handler<?>> handlerMap = new HashMap<>();
    private final Map<Class<?>, Handler<?>> handlerCache = new HashMap<>();

    public JSParserBase() {
        registerHandler(Wrapper.class, WrapperHandler.INSTANCE);
        registerHandler(Iterable.class, IterableParser.INSTANCE);
        registerHandler(GameCollectionJS.class, ContentIdentityHandler.INSTANCE);
    }

    public <Event> EventType.FunctionParameterDescriptor<Event> param(GameContentRegistry.Entry<?, ?, ?, ?> typeEntry) {
        var attributes = AddonUtil.usingOnlyArray(typeEntry);
        return new EventType.FunctionParameterDescriptor<>(attributes, (call, ignored, arg) -> parse(call.context(), arg));
    }

    public GameCollectionJS parse(EventType.FunctionCall<?> call, JSContext cx, Object[] args) {
        return parse(call.context(), args);
    }

    public GameCollectionJS parse(JSContext context, Object @Nullable ... inputs) {
        try {
            return parseInternal(context, inputs);
        } catch (ParseException e) {
            throw new KubeRuntimeException(context.source().toString(), e);
        }
    }

    public GameCollectionJS parse(Context cx, Object @Nullable ... inputs) {
        var context = JSContext.instance(cx);
        return parse(context, inputs);
    }

    @SafeVarargs
    protected final <T> GameCollectionJS parseInternal(JSContext context, T @Nullable ... inputs) throws ParseException {
        var parseQueue = new ArrayDeque<@Nullable Object>(inputs == null ? List.of() : Arrays.asList(inputs));
        var usedHandlers = new HashSet<Handler<?>>();
        var content = new ArrayList<GameContent>();

        for (var input = parseQueue.poll(); input != null; input = parseQueue.poll()) {
            while (true) {
                if (input instanceof GameContentWrapper c) {
                    content.add(c.gameContent());
                    break;
                }

                var handler = getHandler(input.getClass());
                usedHandlers.add(handler);
                input = handler.read(context, input, parseQueue::add);
                if (input instanceof GameContentWrapper c) {
                    content.add(c.gameContent());
                    break;
                }

                if (input == null) break;
            }
        }

        for (var usedHandler : usedHandlers) {
            var c = usedHandler.finish(context);
            if (c != null) content.add(c);
        }

        return new GameCollectionJSImpl(context, GameContentUnion.create(content));
    }

    protected <T, V> void registerRegistryHandlers(Class<T> cls, ResourceKey<? extends Registry<V>> registryKey, Function<T, V> transform, GameContentRegistry.Entry<?, ?, HolderSet<V>, ?> type) {
        registerHandler(cls, new RegistryParser<>(registryKey, transform));
        registerHandler(Holder.class, new RegistryParserCollector<V>(holders -> GameContentDirect.create(type, holders)));
        registerHandler(TagKey.class, new TagParser<>(registryKey, holders -> GameContentDirect.create(type, holders)));
        registerHandler(CharSequence.class, (context, value, parseAppender) -> {
            var registry = context.registryAccess().registryOrThrow(registryKey);
            var string = value.toString();
            if (string.startsWith("@")) {
                return new GameContentTypedMod<>(type, string.substring(1));
            }
            if (string.startsWith("#")) {
                return TagKey.create(registry.key(), ResourceLocation.parse(string.substring(1)));
            }

            if (string.startsWith(".")) string = string.substring(1);
            var o = registry.getHolder(ResourceLocation.parse(string));
            if (o.isPresent()) return o.get();
            throw new NoSuchElementException("Unknown entry " + string + " in registry " + registry.key());
        });
    }

    protected <T> void registerHandler(Class<? extends T> cls, Handler<? super T> handler) {
        handlerMap.put(cls, handler);
    }

    @SuppressWarnings("unchecked")
    private Handler<Object> getHandler(Class<?> cls) throws ParseException {
        if (cls.isArray()) return ArrayHandler.INSTANCE;
        if (handlerCache.containsKey(cls)) {
            return (Handler<@NonNull Object>) handlerCache.get(cls);
        }
        if (handlerMap.containsKey(cls)) {
            handlerCache.put(cls, Objects.requireNonNull(handlerMap.get(cls)));
            return (Handler<@NonNull Object>) handlerCache.get(cls);
        }
        for (var e : handlerMap.entrySet()) {
            if (Objects.requireNonNull(e).getKey().isAssignableFrom(cls)) {
                handlerCache.put(cls, e.getValue());
                return (Handler<@NonNull Object>) e.getValue();
            }
        }
        throw new ParseException("Cannot parse " + cls.getName());
    }

    public interface Handler<T> {
        @Nullable Object read(JSContext context, T value, Consumer<Object> parseAppender) throws ParseException;

        default @Nullable GameContent finish(JSContext context) throws ParseException {
            return null;
        }
    }

    public static class ParseException extends Exception {
        public ParseException(String message) {
            super(message);
        }
    }

    public record ArrayHandler() implements Handler<Object> {
        public static final ArrayHandler INSTANCE = new ArrayHandler();

        @Override
        public @Nullable Object read(JSContext context, Object value, Consumer<Object> parseAppender) {
            var len = Array.getLength(value);
            if (len == 1) return Array.get(value, 0);
            for (var i = 0; i < len; i++) {
                parseAppender.accept(Objects.requireNonNull(Array.get(value, i)));
            }
            return null;
        }
    }

    public record WrapperHandler() implements Handler<Wrapper> {
        public static final WrapperHandler INSTANCE = new WrapperHandler();

        @Override
        public @Nullable Object read(JSContext context, Wrapper value, Consumer<Object> parseAppender) {
            return value.unwrap();
        }
    }

    @SuppressWarnings("rawtypes")
    public record IterableParser() implements Handler<Iterable> {
        public static final IterableParser INSTANCE = new IterableParser();

        @Override
        public @Nullable Object read(JSContext context, Iterable value, Consumer<Object> parseAppender) {
            var it = value.iterator();
            if (!it.hasNext()) return null;
            var first = Objects.requireNonNull(it.next());
            if (!it.hasNext()) return first;
            parseAppender.accept(first);
            do {
                parseAppender.accept(Objects.requireNonNull(it.next()));
            } while (it.hasNext());
            return null;
        }
    }

    public record ContentIdentityHandler() implements Handler<GameCollectionJS> {
        public static final ContentIdentityHandler INSTANCE = new ContentIdentityHandler();

        @Override
        public Object read(JSContext context, GameCollectionJS value, Consumer<Object> parseAppender) {
            return value;
        }
    }

    public static abstract class CollectingHandler<T, V> implements Handler<T> {
        private final Set<V> set = new HashSet<>();

        @Override
        public @Nullable Object read(JSContext context, T value, Consumer<Object> parseAppender) {
            set.add(transform(value));
            return null;
        }

        @Override
        public @Nullable GameContent finish(JSContext context) {
            if (set.isEmpty()) return null;
            var content = finish(context, Set.copyOf(set));
            set.clear();
            return content;
        }

        public abstract V transform(T value);

        public abstract GameContent finish(JSContext context, Set<V> set);
    }

    @SuppressWarnings("rawtypes")
    public static class TagParser<V> extends CollectingHandler<TagKey, TagKey<V>> {
        private final ResourceKey<? extends Registry<? extends V>> registry;
        private final Function<HolderSet<V>, GameContent> contentCreator;

        public TagParser(ResourceKey<? extends Registry<? extends V>> registry, Function<HolderSet<V>, GameContent> contentCreator) {
            this.registry = registry;
            this.contentCreator = contentCreator;
        }

        @SuppressWarnings("unchecked")
        @Override
        public TagKey<V> transform(TagKey value) {
            return (TagKey<V>) value;
        }

        @Override
        public GameContent finish(JSContext context, Set<TagKey<V>> set) {
            var registry = context.registryAccess().registryOrThrow(this.registry);
            if (set.size() == 1) {
                return contentCreator.apply(registry.getTag(set.iterator().next()).orElseThrow());
            }
            HolderSet<V> holderSet = HolderSet.direct(set
                    .stream()
                    .map(registry::getTag)
                    .map(Optional::orElseThrow)
                    .map(Objects::requireNonNull)
                    .flatMap(HolderSet.Named::stream)
                    .toList());
            return contentCreator.apply(holderSet);
        }
    }

    public static class RegistryParser<T, V> implements Handler<T> {
        private final ResourceKey<? extends Registry<? extends V>> registry;
        private final Function<T, V> transform;

        public RegistryParser(ResourceKey<? extends Registry<V>> registry, Function<T, V> transform) {
            this.registry = registry;
            this.transform = transform;
        }

        @Override
        public @Nullable Object read(JSContext context, T value, Consumer<Object> parseAppender) {
            var registry = context.registryAccess().registryOrThrow(this.registry);
            return registry.wrapAsHolder(transform.apply(value));
        }
    }

    @SuppressWarnings("rawtypes")
    public static class RegistryParserCollector<T> extends CollectingHandler<Holder, Holder<T>> {
        private final Function<HolderSet<T>, GameContent> contentCreator;

        public RegistryParserCollector(Function<HolderSet<T>, GameContent> contentCreator) {
            this.contentCreator = contentCreator;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Holder<T> transform(Holder value) {
            return (Holder<T>) value;
        }

        @Override
        public GameContent finish(JSContext context, Set<Holder<T>> set) {
            return contentCreator.apply(HolderSet.direct(List.copyOf(set)));
        }
    }
}
