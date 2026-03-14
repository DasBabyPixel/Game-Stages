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

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class JSParserBase {
    private final Map<Class<?>, Handler<?>> handlerMap = new HashMap<>();
    private final Map<Class<?>, Handler<?>> handlerCache = new HashMap<>();

    public JSParserBase() {
        registerHandler(Wrapper.class, WrapperHandler.INSTANCE);
        registerHandler(Collection.class, CollectionParser.INSTANCE);
        registerHandler(CommonGameContent.class, ContentIdentityHandler.INSTANCE);
    }

    @SuppressWarnings("unchecked")
    public static CommonGameContent parse(List<? extends CommonGameContent> list) {
        if (list.size() == 1) return list.getFirst();
        return new CommonGameContent.Union((List<CommonGameContent>) list);
    }

    public CommonGameContent parse(Context cx, Object... inputs) {
        try {
            var list = parse(inputs);
            return parse(list);
        } catch (ParseException e) {
            throw new KubeRuntimeException(SourceLine.of(cx).toString(), e);
        }
    }

    protected List<CommonGameContent> parse(Object... inputs) throws ParseException {
        var parseQueue = new ArrayDeque<>(Arrays.asList(inputs));
        var usedHandlers = new ArrayList<Handler<?>>();
        var content = new ArrayList<CommonGameContent>();

        for (var input = parseQueue.poll(); input != null; input = parseQueue.poll()) {
            do {
                var handler = getHandler(input.getClass());
                usedHandlers.add(handler);
                input = handler.read(input, parseQueue::add);
                if (input instanceof CommonGameContent c) {
                    content.add(c);
                    input = null;
                }
            } while (input != null);
        }

        for (var usedHandler : usedHandlers) {
            var c = usedHandler.finish();
            if (c != null) content.add(c);
        }

        return content;
    }

    protected <T, V> void registerRegistryHandlers(Class<T> cls, Registry<V> registry, Function<T, V> transform, Function<HolderSet<V>, CommonGameContent> contentCreator, GameContentType<?> type) {
        registerHandler(cls, new RegistryParser<>(registry, transform));
        registerHandler(Holder.class, new RegistryParserCollector<>(contentCreator));
        registerHandler(TagKey.class, new TagParser<>(registry, contentCreator));

        registerHandler(CharSequence.class, (value, parseAppender) -> {
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

    protected <T> void registerHandler(Class<? extends T> cls, Handler<? super T> handler) {
        handlerMap.put(cls, handler);
    }

    @SuppressWarnings("unchecked")
    private Handler<Object> getHandler(Class<?> cls) throws ParseException {
        if (handlerCache.containsKey(cls)) {
            return (Handler<Object>) handlerCache.get(cls);
        }
        if (handlerMap.containsKey(cls)) {
            handlerCache.put(cls, handlerMap.get(cls));
            return (Handler<Object>) handlerCache.get(cls);
        }
        for (var e : handlerMap.entrySet()) {
            if (e.getKey().isAssignableFrom(cls)) {
                handlerCache.put(cls, e.getValue());
                return (Handler<Object>) e.getValue();
            }
        }
        throw new ParseException("Cannot parse " + cls.getName());
    }

    public interface Handler<T> {
        @Nullable Object read(T value, Consumer<Object> parseAppender);

        default @Nullable CommonGameContent finish() {
            return null;
        }
    }

    public static class ParseException extends Exception {
        public ParseException(String message) {
            super(message);
        }
    }

    public record WrapperHandler() implements Handler<Wrapper> {
        public static final WrapperHandler INSTANCE = new WrapperHandler();

        @Override
        public @Nullable Object read(Wrapper value, Consumer<Object> parseAppender) {
            return value.unwrap();
        }
    }

    @SuppressWarnings("rawtypes")
    public record CollectionParser() implements Handler<Collection> {
        public static final CollectionParser INSTANCE = new CollectionParser();

        @Override
        public @Nullable Object read(Collection value, Consumer<Object> parseAppender) {
            if (value.size() == 1) {
                return value.iterator().next();
            }
            for (var o : value) {
                parseAppender.accept(o);
            }
            return null;
        }
    }

    public record ContentIdentityHandler() implements Handler<CommonGameContent> {
        public static final ContentIdentityHandler INSTANCE = new ContentIdentityHandler();

        @Override
        public @Nullable Object read(CommonGameContent value, Consumer<Object> parseAppender) {
            return value;
        }
    }

    public static abstract class CollectingHandler<T, V> implements Handler<T> {
        private final Set<V> set = new HashSet<>();

        @Override
        public @Nullable Object read(T value, Consumer<Object> parseAppender) {
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
        private final Registry<V> registry;
        private final Function<HolderSet<V>, CommonGameContent> contentCreator;

        public TagParser(Registry<V> registry, Function<HolderSet<V>, CommonGameContent> contentCreator) {
            this.registry = registry;
            this.contentCreator = contentCreator;
        }

        @SuppressWarnings("unchecked")
        @Override
        public TagKey<V> transform(TagKey value) {
            return (TagKey<V>) value;
        }

        @Override
        public @NonNull CommonGameContent finish(Set<TagKey<V>> set) {
            if (set.size() == 1) {
                return contentCreator.apply(registry.getTag(set.iterator().next()).orElseThrow());
            }
            HolderSet<V> holderSet = HolderSet.direct(set
                    .stream()
                    .map(registry::getTag)
                    .map(Optional::orElseThrow)
                    .flatMap(HolderSet::stream)
                    .toList());
            return contentCreator.apply(holderSet);
        }
    }

    public static class RegistryParser<T, V> implements Handler<T> {
        private final Registry<V> registry;
        private final Function<T, V> transform;

        public RegistryParser(Registry<V> registry, Function<T, V> transform) {
            this.registry = registry;
            this.transform = transform;
        }

        @Override
        public @Nullable Object read(T value, Consumer<Object> parseAppender) {
            return registry.wrapAsHolder(transform.apply(value));
        }
    }

    @SuppressWarnings("rawtypes")
    public static class RegistryParserCollector<T> extends CollectingHandler<Holder, Holder<T>> {
        private final Function<HolderSet<T>, CommonGameContent> contentCreator;

        public RegistryParserCollector(Function<HolderSet<T>, CommonGameContent> contentCreator) {
            this.contentCreator = contentCreator;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Holder<T> transform(Holder value) {
            return (Holder<T>) value;
        }

        @Override
        public @NonNull CommonGameContent finish(Set<Holder<T>> set) {
            return contentCreator.apply(HolderSet.direct(List.copyOf(set)));
        }
    }
}
