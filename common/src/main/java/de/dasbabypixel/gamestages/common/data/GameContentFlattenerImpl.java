package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.CommonInstances;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@NullMarked
public class GameContentFlattenerImpl implements GameContentFlattener {
    private final Map<GameContent, GameContentSimple> simpleCache = new HashMap<>();
    private final Map<TypedCacheEntry, TypedValue> typedCache = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public GameContentSimple flatten(GameContentWrapper contentWrapper) {
        var content = contentWrapper.gameContent();
        if (simpleCache.containsKey(content)) {
            return Objects.requireNonNull(simpleCache.get(content));
        }
        var v = switch (content) {
            case GameContentExcept(var base_, var exclusion_) -> {
                var base = flatten(base_);
                var exclusionsByType = new HashMap<GameContentRegistry.Entry<?, ?, ?, ?>, Set<Object>>();
                for (var entry : flatten(exclusion_).entries()) {
                    exclusionsByType
                            .computeIfAbsent(entry.typeEntry(), ignored -> new HashSet<>())
                            .add(convertToList(entry));
                }
                var entryList = new ArrayList<GameContentSimple.TypeEntry<?>>();
                for (var entry : base.entries()) {
                    var e = exclude(entry, exclusionsByType.get(entry.typeEntry()));
                    if (e != null) entryList.add(e);
                }
                yield new GameContentSimple(entryList);
            }
            case GameContentOnly(var base_, var inclusion_) -> {
                var base = flatten(base_);
                var inclusionsByType = new HashMap<GameContentRegistry.Entry<?, ?, ?, ?>, Set<Object>>();
                for (var entry : flatten(inclusion_).entries()) {
                    inclusionsByType
                            .computeIfAbsent(entry.typeEntry(), ignored -> new HashSet<>())
                            .add(convertToList(entry));
                }
                var entryList = new ArrayList<GameContentSimple.TypeEntry<?>>();
                for (var entry : base.entries()) {
                    var e = include(entry, inclusionsByType.get(entry.typeEntry()));
                    if (e != null) entryList.add(e);
                }
                yield new GameContentSimple(entryList);
            }
            case GameContentSimple simple -> simple;
            case GameContentUnion(var list) -> {
                var contentByType = new HashMap<GameContentRegistry.Entry<?, ?, ?, ?>, Set<Object>>();
                for (var gameContent_ : list) {
                    var gameContent = flatten(gameContent_);
                    for (var entry : gameContent.entries()) {
                        var set = contentByType.computeIfAbsent(entry.typeEntry(), ignored -> new HashSet<>());
                        set.add(entry.elements());
                    }
                }
                var entries = new ArrayList<GameContentSimple.TypeEntry<?>>();
                for (var e : contentByType.entrySet()) {
                    Objects.requireNonNull(e);
                    var type = (GameContentRegistry.Entry<?, ?, @NonNull Object, @NonNull Object>) e.getKey();
                    var set = e.getValue();
                    if (set.size() == 1) {
                        entries.add(new GameContentSimple.TypeEntry<>(type, set.iterator().next()));
                    } else {
                        var builder = type.type().newElementsBuilder();
                        for (var o : set) {
                            builder.addElements(o);
                        }
                        entries.add(new GameContentSimple.TypeEntry<>(type, builder.build()));
                    }
                }
                yield new GameContentSimple(entries);
            }
            case GameContentFilterType<?, ?, ?> filterType ->
                    flatten(flatten(filterType.base(), filterType.typeEntry()));
            case GameContentDirect<?, ?, ?> direct -> new GameContentSimple(List.of(direct.createTypeEntry()));
            case GameContentMod(var modId) -> {
                var typeEntries = new ArrayList<GameContentSimple.TypeEntry<?>>();
                for (var entry : CommonInstances.gameContentRegistry.entries()) {
                    var e = modContent(entry, modId);
                    if (e != null) typeEntries.add(e);
                }
                yield new GameContentSimple(typeEntries);
            }
            case GameContentSugar sugar -> flatten(sugar.desugar());
        };
        simpleCache.put(content, v);
        return v;
    }

    private <Elements> GameContentSimple.@Nullable TypeEntry<Elements> modContent(GameContentRegistry.Entry<?, ?, Elements, ?> type, String modId) {
        var entry = new GameContentSimple.TypeEntry<>(type, type.type().modContent(modId));
        if (!type.type().iterate(entry.elements()).iterator().hasNext()) return null;
        return entry;
    }

    private <E> GameContentSimple.@Nullable TypeEntry<?> exclude(GameContentSimple.TypeEntry<E> entry, @Nullable Set<Object> exclusions) {
        if (exclusions == null || exclusions.isEmpty()) return entry;
        return excludeInclude(entry.typeEntry(), entry.elements(), exclusions, false);
    }

    private <E> GameContentSimple.@Nullable TypeEntry<?> include(GameContentSimple.TypeEntry<E> entry, @Nullable Set<Object> inclusions) {
        if (inclusions == null || inclusions.isEmpty()) return null;
        return excludeInclude(entry.typeEntry(), entry.elements(), inclusions, true);
    }

    private <Elements, Element> GameContentSimple.@Nullable TypeEntry<Elements> excludeInclude(GameContentRegistry.Entry<?, ?, Elements, Element> type, Elements elements, Set<Object> data, boolean include) {
        boolean diverges = false;
        for (var element : type.type().iterate(elements)) {
            // excludes.contains(element) == true -> diverges
            // includes.contains(element) == false -> diverges
            if (data.contains(element) != include) {
                diverges = true;
                break;
            }
        }
        if (!diverges) return new GameContentSimple.TypeEntry<>(type, elements);
        var builder = type.type().newElementsBuilder();
        for (var element : type.type().iterate(elements)) {
            // excludes.contains(element) == false -> include
            // includes.contains(element) == true -> include
            if (data.contains(element) == include) {
                builder.addElement(element);
            }
        }
        var built = builder.build();
        var empty = !type.type().iterate(built).iterator().hasNext();
        if (empty) return null;
        return new GameContentSimple.TypeEntry<>(type, built);
    }

    private <Elements> List<Object> convertToList(GameContentSimple.TypeEntry<Elements> entry) {
        var list = new ArrayList<>();
        entry.typeEntry().type().iterate(entry.elements()).iterator().forEachRemaining(list::add);
        return list;
    }

    @Override
    public <TypeData, Elements, Element> GameContentDirect<TypeData, Elements, Element> flatten(GameContentWrapper content, GameContentRegistry.Entry<?, TypeData, Elements, Element> type) {
        var flattened = flatten0(content.gameContent(), type);
        if (flattened == null) return type.empty();
        return flattened;
    }

    @SuppressWarnings("unchecked")
    public <TypeData, Elements, Element> @Nullable GameContentDirect<TypeData, Elements, Element> flatten0(GameContent content, GameContentRegistry.Entry<?, TypeData, Elements, Element> type) {
        var cacheEntry = new TypedCacheEntry(type, content);
        if (typedCache.containsKey(cacheEntry)) {
            return (GameContentDirect<TypeData, Elements, Element>) Objects
                    .requireNonNull(typedCache.get(cacheEntry))
                    .content();
        }
        var v = switch (content) {
            case GameContentExcept(var base_, var exclusion_) -> {
                var base = flatten(base_, type);
                if (!type.type().iterate(base.elements()).iterator().hasNext()) yield null; // Empty base
                var exclusion = flatten(exclusion_, type);
                if (!type.type().iterate(exclusion.elements()).iterator().hasNext()) yield base; // Empty exclusions
                var exclusionSet = new HashSet<Element>();
                type.type().iterate(exclusion.elements()).forEach(exclusionSet::add);
                boolean anyExcluded = false;
                var builder = type.type().newElementsBuilder();
                for (var element : type.type().iterate(base.elements())) {
                    if (exclusionSet.contains(element)) {
                        anyExcluded = true;
                    } else {
                        builder.addElement(element);
                    }
                }
                if (anyExcluded) {
                    yield GameContentDirect.create(type, builder.build());
                }
                yield base;
            }
            case GameContentOnly(var base_, var inclusion_) -> {
                var base = flatten(base_, type);
                if (!type.type().iterate(base.elements()).iterator().hasNext()) yield null; // Empty base
                var inclusion = flatten(inclusion_, type);
                if (!type.type().iterate(inclusion.elements()).iterator().hasNext()) yield null; // empty inclusion
                var inclusionSet = new HashSet<Element>();
                type.type().iterate(inclusion.elements()).forEach(inclusionSet::add);
                boolean anyExcluded = false;
                var builder = type.type().newElementsBuilder();
                for (var element : type.type().iterate(base.elements())) {
                    if (inclusionSet.contains(element)) {
                        builder.addElement(element);
                    } else {
                        anyExcluded = true;
                    }
                }
                if (anyExcluded) {
                    yield GameContentDirect.create(type, builder.build());
                }
                yield base;
            }
            case GameContentSimple simple -> {
                var elements = simple.content(type);
                if (elements == null) elements = type.type().newElementsBuilder().build();
                yield GameContentDirect.create(type, elements);
            }
            case GameContentUnion(var list) -> {
                var builder = type.type().newElementsBuilder();
                for (var e : list) {
                    var c = flatten0(e, type);
                    if (c != null) builder.addElements(c.elements());
                }
                yield GameContentDirect.create(type, builder.build());
            }
            case GameContentFilterType<?, ?, ?> filterType -> {
                if (filterType.typeEntry() == type) {
                    yield flatten0(filterType.base(), type);
                }
                yield null;
            }
            case GameContentDirect<?, ?, ?> direct -> {
                GameContentDirect<TypeData, Elements, Element> data = null;
                if (direct.typeEntry() == type) {
                    data = (GameContentDirect<TypeData, Elements, Element>) direct;
                }
                yield data;
            }
            case GameContentMod(var modId) -> {
                var elements = type.type().modContent(modId);
                if (!type.type().iterate(elements).iterator().hasNext()) yield null;
                yield GameContentDirect.create(type, elements);
            }
            case GameContentSugar sugar -> flatten0(sugar.desugar(), type);
        };
        var val = new TypedValue(v);
        typedCache.put(cacheEntry, val);
        return v;
    }

    private record TypedCacheEntry(GameContentRegistry.Entry<?, ?, ?, ?> type, GameContent content) {
    }

    private record TypedValue(@Nullable GameContentDirect<?, ?, ?> content) {
    }
}
