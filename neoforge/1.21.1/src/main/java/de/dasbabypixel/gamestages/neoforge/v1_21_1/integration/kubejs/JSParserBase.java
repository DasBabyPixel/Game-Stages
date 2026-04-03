package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs;

import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonGameContent;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class JSParserBase {
    private final Map<@NonNull Class<?>, @NonNull Handler<?>> handlerMap = new HashMap<>();
    private final Map<@NonNull Class<?>, @NonNull Handler<?>> handlerCache = new HashMap<>();

    public JSParserBase() {
        registerHandler(Wrapper.class, WrapperHandler.INSTANCE);
        registerHandler(ContentWrapper.class, ContentWrapperHandler.INSTANCE);
        registerHandler(Iterable.class, IterableParser.INSTANCE);
        registerHandler(CommonGameContent.class, ContentIdentityHandler.INSTANCE);
    }

    @SafeVarargs
    public final <T> @NonNull CommonGameContent parse(@NonNull Context cx, @NonNull T @NonNull ... inputs) {
        try {
            var list = parse(inputs);
            return parse(list);
        } catch (ParseException e) {
            throw new KubeRuntimeException(Objects.requireNonNull(SourceLine.of(cx)).toString(), e);
        }
    }

    @SafeVarargs
    protected final <T> @NonNull List<@NonNull CommonGameContent> parse(@NonNull T @NonNull ... inputs) throws ParseException {
        var parseQueue = new ArrayDeque<@NonNull Object>(Arrays.asList(inputs));
        var usedHandlers = new ArrayList<@NonNull Handler<?>>();
        var content = new ArrayList<@NonNull CommonGameContent>();

        for (var input = parseQueue.poll(); input != null; input = parseQueue.poll()) {
            while (true) {
                if (input instanceof CommonGameContent c) {
                    content.add(c);
                    break;
                }

                var handler = getHandler(input.getClass());
                usedHandlers.add(handler);
                input = handler.read(input, parseQueue::add);
                if (input instanceof CommonGameContent c) {
                    content.add(c);
                    break;
                }

                if (input == null) break;
            }
        }

        for (var usedHandler : usedHandlers) {
            var c = usedHandler.finish();
            if (c != null) content.add(c);
        }

        return content;
    }

    protected <T, V> void registerRegistryHandlers(@NonNull Class<T> cls, @NonNull Registry<V> registry, @NonNull Function<T, V> transform, @NonNull Function<HolderSet<V>, CommonGameContent> contentCreator, @NonNull GameContentType<?> type) {
        registerHandler(cls, new RegistryParser<>(registry, transform));
        registerHandler(Holder.class, new RegistryParserCollector<>(contentCreator));
        registerHandler(TagKey.class, new TagParser<>(registry, contentCreator));
        this.<@NonNull CharSequence>registerHandler(CharSequence.class, (value, parseAppender) -> {
            var string = value.toString();
            if (string.startsWith("@")) {
                return new CommonGameContent.Mod(string.substring(1)).filterType(type);
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

    protected <T> void registerHandler(@NonNull Class<? extends T> cls, @NonNull Handler<? super T> handler) {
        handlerMap.put(cls, handler);
    }

    @SuppressWarnings("unchecked")
    private @NonNull Handler<Object> getHandler(@NonNull Class<?> cls) throws ParseException {
        if (cls.isArray()) return ArrayHandler.INSTANCE;
        if (handlerCache.containsKey(cls)) {
            return (Handler<Object>) handlerCache.get(cls);
        }
        if (handlerMap.containsKey(cls)) {
            handlerCache.put(cls, Objects.requireNonNull(handlerMap.get(cls)));
            return (Handler<Object>) handlerCache.get(cls);
        }
        for (var e : handlerMap.entrySet()) {
            if (Objects.requireNonNull(e).getKey().isAssignableFrom(cls)) {
                handlerCache.put(cls, e.getValue());
                return (Handler<Object>) e.getValue();
            }
        }
        throw new ParseException("Cannot parse " + cls.getName());
    }

    @SuppressWarnings("unchecked")
    public static @NonNull CommonGameContent parse(@NonNull List<? extends @NonNull CommonGameContent> list) {
        if (list.size() == 1) return list.getFirst();
        return new CommonGameContent.Union((List<CommonGameContent>) list);
    }

    public interface Handler<T> {
        @Nullable Object read(T value, @NonNull Consumer<@NonNull Object> parseAppender);

        default @Nullable CommonGameContent finish() {
            return null;
        }
    }

    public static class ParseException extends Exception {
        public ParseException(String message) {
            super(message);
        }
    }

    public record ArrayHandler() implements Handler<@NonNull Object> {
        public static final ArrayHandler INSTANCE = new ArrayHandler();

        @Override
        public @Nullable Object read(@NonNull Object value, @NonNull Consumer<@NonNull Object> parseAppender) {
            var len = Array.getLength(value);
            if (len == 1) return Array.get(value, 0);
            for (var i = 0; i < len; i++) {
                parseAppender.accept(Objects.requireNonNull(Array.get(value, i)));
            }
            return null;
        }
    }

    public record ContentWrapperHandler() implements Handler<@NonNull ContentWrapper> {
        public static final ContentWrapperHandler INSTANCE = new ContentWrapperHandler();

        @Override
        public @Nullable Object read(@NonNull ContentWrapper value, @NonNull Consumer<@NonNull Object> parseAppender) {
            return value.content();
        }
    }

    public record WrapperHandler() implements Handler<@NonNull Wrapper> {
        public static final WrapperHandler INSTANCE = new WrapperHandler();

        @Override
        public @Nullable Object read(@NonNull Wrapper value, @NonNull Consumer<@NonNull Object> parseAppender) {
            return value.unwrap();
        }
    }

    @SuppressWarnings("rawtypes")
    public record IterableParser() implements Handler<@NonNull Iterable> {
        public static final IterableParser INSTANCE = new IterableParser();

        @Override
        public @Nullable Object read(@NonNull Iterable value, @NonNull Consumer<@NonNull Object> parseAppender) {
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

    public record ContentIdentityHandler() implements Handler<CommonGameContent> {
        public static final ContentIdentityHandler INSTANCE = new ContentIdentityHandler();

        @Override
        public @Nullable Object read(CommonGameContent value, @NonNull Consumer<@NonNull Object> parseAppender) {
            return value;
        }
    }

    public static abstract class CollectingHandler<T, V> implements Handler<T> {
        private final Set<V> set = new HashSet<>();

        @Override
        public @Nullable Object read(T value, @NonNull Consumer<@NonNull Object> parseAppender) {
            set.add(transform(value));
            return null;
        }

        @Override
        public @Nullable CommonGameContent finish() {
            if (set.isEmpty()) return null;
            return finish(set);
        }

        public abstract V transform(T value);

        public abstract @NonNull CommonGameContent finish(Set<V> set);
    }

    @SuppressWarnings("rawtypes")
    public static class TagParser<V> extends CollectingHandler<TagKey, TagKey<V>> {
        private final @NonNull Registry<V> registry;
        private final @NonNull Function<@NonNull HolderSet<V>, @NonNull CommonGameContent> contentCreator;

        public TagParser(@NonNull Registry<V> registry, @NonNull Function<@NonNull HolderSet<V>, @NonNull CommonGameContent> contentCreator) {
            this.registry = registry;
            this.contentCreator = contentCreator;
        }

        @SuppressWarnings("unchecked")
        @Override
        public TagKey<V> transform(@NonNull TagKey value) {
            return (TagKey<V>) value;
        }

        @Override
        public @NonNull CommonGameContent finish(@NonNull Set<@NonNull TagKey<V>> set) {
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
        private final @NonNull Registry<V> registry;
        private final @NonNull Function<T, V> transform;

        public RegistryParser(@NonNull Registry<V> registry, @NonNull Function<T, V> transform) {
            this.registry = registry;
            this.transform = transform;
        }

        @Override
        public @Nullable Object read(T value, @NonNull Consumer<@NonNull Object> parseAppender) {
            return registry.wrapAsHolder(transform.apply(value));
        }
    }

    @SuppressWarnings("rawtypes")
    public static class RegistryParserCollector<T> extends CollectingHandler<Holder, Holder<T>> {
        private final @NonNull Function<@NonNull HolderSet<T>, @NonNull CommonGameContent> contentCreator;

        public RegistryParserCollector(@NonNull Function<@NonNull HolderSet<T>, @NonNull CommonGameContent> contentCreator) {
            this.contentCreator = contentCreator;
        }

        @SuppressWarnings("unchecked")
        @Override
        public @NonNull Holder<T> transform(@NonNull Holder value) {
            return (Holder<T>) value;
        }

        @Override
        public @NonNull CommonGameContent finish(@NonNull Set<@NonNull Holder<T>> set) {
            return contentCreator.apply(HolderSet.direct(List.copyOf(set)));
        }
    }
}
