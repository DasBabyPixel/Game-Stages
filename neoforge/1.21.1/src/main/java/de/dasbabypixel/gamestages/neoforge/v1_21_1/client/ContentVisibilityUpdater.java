package de.dasbabypixel.gamestages.neoforge.v1_21_1.client;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.addon.ClientEvents;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import net.neoforged.fml.util.thread.EffectiveSide;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@NullMarked
public abstract class ContentVisibilityUpdater<WrapperData, RawData, Entry extends CompiledRestrictionEntry<? extends Entry, ?>> {
    private final GameContentType<?> type;
    private final Set<RawData> invisible = new HashSet<>();
    private Map<RawData, WrapperData> wrapperByRawMap = Map.of();
    private Map<CompiledRestrictionPredicate, Set<WrapperData>> affectedByPredicateMap = Map.of();

    public ContentVisibilityUpdater(GameContentType<?> type) {
        this.type = type;
        Addon.CLIENT_RECOMPILE_POST_EVENT.addListener(this::postRecompile);
        ClientEvents.CLIENT_DISABLE.addListener(this::onDisable);
    }

    private void onDisable(ClientEvents.ClientDisableEvent event) {
        invisible.clear();
        wrapperByRawMap = Map.of();
        affectedByPredicateMap = Map.of();
    }

    private void postRecompile(Addon.ClientRecompilePostEvent event) {
        if (!Objects.requireNonNull(EffectiveSide.get()).isClient()) throw new IllegalStateException();
        var stages = event.stages();

        var compileIndex = stages.get(BaseStages.CompileIndex.ATTRIBUTE);

        var collector = new Collector();
        preCollect();
        collect(stages, compileIndex, collector);
        postCollect();
        {
            var byRaw = new HashMap<RawData, WrapperData>();
            for (var rawData : collector.dataSet) {
                var wrapper = createWrapper(rawData, List.copyOf(Objects.requireNonNull(collector.affectedByDataMap.get(rawData))));
                byRaw.put(rawData, wrapper);
            }
            wrapperByRawMap = Map.copyOf(byRaw);
        }

        {
            var a = new HashMap<CompiledRestrictionPredicate, Set<WrapperData>>();
            for (var e : collector.affectedByPredicateMap.entrySet()) {
                Objects.requireNonNull(e);
                a.put(e.getKey(), toSet(e.getValue()));
            }
            affectedByPredicateMap = Map.copyOf(a);
        }

        // Following scenario:
        // Some content is hidden by a restriction.
        // That restriction is deleted, then stages are compiled again.
        // The content won't show up in the collected content, because it's not affected anymore.
        // It is still hidden in this updater though. We need to unhide that content.
        // This content is exactly the old/current "invisible" collection
        if (!invisible.isEmpty()) {
            var toShow = new HashSet<>(invisible);
            invisible.clear();
            show(List.copyOf(toShow));
        }

        update(toSet(collector.dataSet));

        registerUpdateNotifiers();
    }

    protected void preCollect() {
    }

    protected void postCollect() {
    }

    private WrapperData wrapper(RawData raw) {
        return Objects.requireNonNull(wrapperByRawMap.get(raw));
    }

    private Set<WrapperData> toSet(Collection<? extends RawData> data) {
        return Set.copyOf(data.stream().map(this::wrapper).collect(Collectors.toSet()));
    }

    public void viewerStartup() {
        if (!Objects.requireNonNull(EffectiveSide.get()).isClient()) throw new IllegalStateException();
        // Full reload. All are assumed visible again for the viewer, so we need to re-hide all invisible
        if (!invisible.isEmpty()) {
            hide(Objects.requireNonNull(List.copyOf(invisible)));
        }
    }

    private void update(Collection<WrapperData> affected) {
        invalidateCache(affected);
        var newlyVisible = new ArrayList<RawData>();
        var newlyInvisible = new ArrayList<RawData>();
        for (var data : affected) {
            var raw = extract(data);
            if (shouldBeVisible(data)) {
                if (invisible.contains(raw)) newlyVisible.add(raw);
            } else {
                if (!invisible.contains(raw)) newlyInvisible.add(raw);
            }
        }
        update(List.copyOf(newlyVisible), List.copyOf(newlyInvisible));
    }

    protected void invalidateCache(Collection<WrapperData> affected) {
    }

    private void update(List<RawData> newlyVisible, List<RawData> newlyInvisible) {
        if (!newlyVisible.isEmpty()) {
            newlyVisible.forEach(invisible::remove);
        }
        if (!newlyInvisible.isEmpty()) {
            invisible.addAll(newlyInvisible);
        }
        if (!newlyVisible.isEmpty()) show(newlyVisible);
        if (!newlyInvisible.isEmpty()) hide(newlyInvisible);
    }

    @SuppressWarnings("unchecked")
    private void collect(BaseStages stages, BaseStages.CompileIndex compileIndex, Collector collector) {
        for (var compiled : compileIndex.typeIndex(type).entries()) {
            Entry entry = (Entry) compiled;
            collect(stages, compileIndex, entry, collector);
        }
    }

    private void registerUpdateNotifiers() {
        for (var e : affectedByPredicateMap.entrySet()) {
            Objects.requireNonNull(e);
            var list = Objects.requireNonNull(e.getValue());
            e.getKey().addNotifier(newTest -> update(list));
        }
    }

    protected abstract boolean shouldBeVisible(WrapperData data);

    /**
     * Collects all Data that are associated with visibility logic.
     * It does not yet matter whether the data is actually visible,
     * this just collects all which can become visible/invisible at some point.
     */
    protected abstract void collect(BaseStages stages, BaseStages.CompileIndex compileIndex, Entry compiledEntry, Collector collector);

    protected abstract void show(List<RawData> show);

    protected abstract void hide(List<RawData> hide);

    protected abstract RawData extract(WrapperData wrapperData);

    protected abstract WrapperData createWrapper(RawData rawData, List<CompiledRestrictionPredicate> relevantEntries);

    public final class Collector {
        private final Set<RawData> dataSet = new HashSet<>();
        private final Map<CompiledRestrictionPredicate, Set<RawData>> affectedByPredicateMap = new HashMap<>();
        private final Map<RawData, Set<CompiledRestrictionPredicate>> affectedByDataMap = new HashMap<>();

        public void add(CompiledRestrictionPredicate predicate, RawData data) {
            dataSet.add(data);
            affectedByPredicateMap.computeIfAbsent(predicate, e -> new HashSet<>()).add(data);
            affectedByDataMap.computeIfAbsent(data, e -> new HashSet<>()).add(predicate);
        }
    }
}
