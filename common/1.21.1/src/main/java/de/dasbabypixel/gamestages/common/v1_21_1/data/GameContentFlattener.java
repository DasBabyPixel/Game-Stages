package de.dasbabypixel.gamestages.common.v1_21_1.data;

import de.dasbabypixel.gamestages.common.data.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class GameContentFlattener {
    public static final AbstractGameStageManager.Attribute<GameContentFlattener> ATTRIBUTE = new AbstractGameStageManager.Attribute<>(GameContentFlattener::new);
    private static final List<FlattenerFactory<?>> FACTORIES = new ArrayList<>();
    private static final Map<GameContentType<?>, FlattenerFactory<?>> FACTORY_BY_TYPE = new HashMap<>();
    private static final FlattenedGameContent EMPTY_FLATTENED;

    static {
        FACTORIES.add(new ItemFlattenerFactory());

        var emptyMap = new HashMap<GameContentType<?>, TypedGameContent>();
        for (var factory : FACTORIES) {
            FACTORY_BY_TYPE.put(factory.type(), factory);
            emptyMap.put(factory.type(), factory.createUnion().complete());
        }
        EMPTY_FLATTENED = new FlattenedGameContent(emptyMap);
    }

    private final Map<GameContent, FlattenedGameContent> cache = new HashMap<>();
    private final Map<GameContentType<?>, Map<GameContent, Object>> typeCache = new HashMap<>();

    private GameContentFlattener() {
    }

    private static void fill(List<Flattener0<?>> flatteners, @Nullable GameContentType<?> requestType, Function<FlattenerFactory<?>, Flattener0<?>> fun) {
        if (requestType != null) {
            flatteners.add(fun.apply(Objects.requireNonNull(FACTORY_BY_TYPE.get(requestType))));
            return;
        }
        for (var factory : FACTORIES) {
            flatteners.add(fun.apply(factory));
        }
    }

    private static void accept(List<Flattener0<?>> flatteners, @Nullable GameContentType<?> requestType, Object content) {
        for (var flattener : flatteners) {
            flattener.accept(requestType, content);
        }
    }

    private FlattenedGameContent cache(GameContent input, FlattenedGameContent content) {
        cache.put(input, content);
        return content;
    }

    private <T extends TypedGameContent> T cache(GameContent input, GameContentType<T> type, T value) {
        typeCache.computeIfAbsent(type, unused -> new HashMap<>()).put(input, value);
        return value;
    }

    public @NonNull FlattenedGameContent flatten(@NonNull GameContent gameContent) {
        if (cache.containsKey(gameContent)) return Objects.requireNonNull(cache.get(gameContent));
        return cache(gameContent, (FlattenedGameContent) flattenInternal(gameContent, null));
    }

    @SuppressWarnings("unchecked")
    public <T extends TypedGameContent> @NonNull T flatten(@NonNull GameContent gameContent, @NonNull GameContentType<T> requestType) {
        if (typeCache.containsKey(requestType) && typeCache.get(requestType).containsKey(gameContent)) {
            return (T) Objects.requireNonNull(typeCache.get(requestType).get(gameContent));
        }
        return cache(gameContent, requestType, (T) flattenInternal(gameContent, requestType));
    }

    private Object flatten0(@NonNull GameContent gameContent, @Nullable GameContentType<?> requestType) {
        if (requestType == null) return flatten(gameContent);
        else return flatten(gameContent, requestType);
    }

    private @NonNull Object flattenInternal(@NonNull GameContent gameContent, @Nullable GameContentType<?> requestType) {
        switch (gameContent) {
            case CommonGameContent.Composite composite -> {
                var flatteners = new ArrayList<Flattener0<?>>(1);
                fill(flatteners, requestType, Flattener0::union);
                var content = composite.content();
                for (var c : content) {
                    accept(flatteners, requestType, flatten0(c, requestType));
                }
                return completeFlattening(flatteners, requestType);
            }
            case CommonGameContent.Only(var base, var inclusions) -> {
                var flatteners = new ArrayList<Flattener0<?>>(1);
                fill(flatteners, requestType, Flattener0::only);
                accept(flatteners, requestType, flatten0(base, requestType));
                for (var inclusion : inclusions) {
                    accept(flatteners, requestType, flatten0(inclusion, requestType));
                }
                return completeFlattening(flatteners, requestType);
            }
            case CommonGameContent.Except(var base, var exclusions) -> {
                var flatteners = new ArrayList<Flattener0<?>>(1);
                fill(flatteners, requestType, Flattener0::except);
                accept(flatteners, requestType, flatten0(base, requestType));
                for (var exclusion : exclusions) {
                    accept(flatteners, requestType, flatten0(exclusion, requestType));
                }
                return completeFlattening(flatteners, requestType);
            }
            case CommonGameContent.FilterType(var base, var type) -> {
                return flatten(base, type);
            }
            case TypedGameContent typed -> {
                var type = typed.type();
                if (requestType == type) return typed;
                if (requestType != null) return EMPTY_FLATTENED;

                var map = new HashMap<GameContentType<?>, TypedGameContent>();
                for (var factory : FACTORIES) {
                    if (factory.type() != type) {
                        map.put(factory.type(), factory.createUnion().complete());
                    } else {
                        map.put(type, typed);
                    }
                }

                return new FlattenedGameContent(map);
            }
            default -> throw new IllegalArgumentException("GameContent has illegal type: " + gameContent
                    .getClass()
                    .getName());
        }
    }

    private Object completeFlattening(List<Flattener0<?>> flatteners, GameContentType<?> requestType) {
        if (requestType != null) {
            return flatteners.getFirst().flattener().complete();
        }
        var map = new HashMap<GameContentType<?>, TypedGameContent>();
        for (var flattener : flatteners) {
            map.put(flattener.type(), flattener.flattener().complete());
        }
        return new FlattenedGameContent(map);
    }

    public interface FlattenerFactory<T extends TypedGameContent> {
        @NonNull GameContentType<T> type();

        @NonNull Flattener<T> createUnion();

        @NonNull Flattener<T> createOnly();

        @NonNull Flattener<T> createExcept();
    }

    public interface Flattener<T extends TypedGameContent> {
        void accept(@NonNull T list);

        @NonNull T complete();
    }

    private record Flattener0<T extends TypedGameContent>(GameContentType<T> type, Flattener<T> flattener) {
        private static <T extends TypedGameContent> Flattener0<T> union(FlattenerFactory<T> factory) {
            return new Flattener0<>(factory.type(), factory.createUnion());
        }

        private static <T extends TypedGameContent> Flattener0<T> only(FlattenerFactory<T> factory) {
            return new Flattener0<>(factory.type(), factory.createOnly());
        }

        private static <T extends TypedGameContent> Flattener0<T> except(FlattenerFactory<T> factory) {
            return new Flattener0<>(factory.type(), factory.createExcept());
        }

        @SuppressWarnings("unchecked")
        private void accept(@Nullable GameContentType<?> requestType, Object content) {
            if (requestType == null) {
                flattener.accept(((FlattenedGameContent) content).get(type));
            } else {
                flattener.accept((T) content);
            }
        }
    }
}
