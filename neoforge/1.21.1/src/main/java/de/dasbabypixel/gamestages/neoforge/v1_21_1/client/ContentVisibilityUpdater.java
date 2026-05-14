package de.dasbabypixel.gamestages.neoforge.v1_21_1.client;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@NullMarked
public abstract class ContentVisibilityUpdater<Data, Entry extends CompiledRestrictionEntry<? extends Entry, ?>> {
    private final GameContentType<?> type;
    private final Set<Data> visible = new HashSet<>();
    private final Set<Data> invisible = new HashSet<>();

    public ContentVisibilityUpdater(GameContentType<?> type) {
        this.type = type;
        Addon.CLIENT_RECOMPILE_PRE_EVENT.addListener(this::preRecompile);
        Addon.CLIENT_RECOMPILE_POST_EVENT.addListener(this::postRecompile);
    }

    private void preRecompile(Addon.ClientRecompilePreEvent event) {
    }

    private void postRecompile(Addon.ClientRecompilePostEvent event) {
        reconfigure(event.stages(), false);

        var compileIndex = event.stages().get(BaseStages.CompileIndex.ATTRIBUTE);
        registerUpdateNotifier(event.stages(), compileIndex);
    }

    public void fullReconfigure(BaseStages stages) {
        invisible.clear();
        visible.clear();
        reconfigure(stages, true);
    }

    private void reconfigure(BaseStages stages, boolean allVisible) {
        Collected<Data> collected;
        if (stages.has(BaseStages.CompileIndex.ATTRIBUTE)) {
            var compileIndex = stages.get(BaseStages.CompileIndex.ATTRIBUTE);
            var collector = new Collector<Data>();
            collect(stages, compileIndex, collector);

            // We need to show all previously invisible, otherwise they could stay invisible
            collector.hideCache.forEach(invisible::remove);
            collector.showCache.addAll(invisible);

            collected = new Collected<>(collector.showCache, collector.hideCache);
        } else {
            collected = new Collected<>(List.of(), List.of());
        }

        visible.clear();
        invisible.clear();

        visible.addAll(collected.showCache);
        invisible.addAll(collected.hideCache);

        if (!allVisible && !collected.showCache.isEmpty()) {
            show(collected.showCache);
        }
        if (!collected.hideCache.isEmpty()) {
            hide(collected.hideCache);
        }
    }

    private void collect(BaseStages stages, BaseStages.CompileIndex compileIndex, Collector<Data> collector) {
        collect(stages, compileIndex, type, collector);
    }

    @SuppressWarnings("unchecked")
    private void collect(BaseStages stages, BaseStages.CompileIndex compileIndex, GameContentType<?> type, Collector<Data> collector) {
        for (var compiled : compileIndex.typeIndex(type).contentListByEntry().keySet()) {
            collect(stages, compileIndex, (Entry) compiled, collector);
        }
    }

    @SuppressWarnings("unchecked")
    private void registerUpdateNotifier(BaseStages stages, BaseStages.CompileIndex compileIndex) {
        var typeIndex = compileIndex.typeIndex(type);
        var entries = (List<Entry>) List.copyOf(typeIndex.contentListByEntry().keySet());
        var byPredicate = new HashMap<CompiledRestrictionPredicate, List<Data>>();
        registerUpdateNotifier(stages, compileIndex, entries, new UpdateRegistrar<>() {
            @Override
            public void register(CompiledRestrictionPredicate predicate, Data data) {
                byPredicate.computeIfAbsent(predicate, i -> new ArrayList<>()).add(data);
            }

            @Override
            public void register(CompiledRestrictionPredicate predicate, Collection<? extends Data> data) {
                byPredicate.computeIfAbsent(predicate, i -> new ArrayList<>()).addAll(data);
            }
        });

        for (var e : byPredicate.entrySet()) {
            Objects.requireNonNull(e);
            var list = Objects.requireNonNull(e.getValue());
            e.getKey().addNotifier(newTest -> {
                if (newTest) {
                    list.forEach(invisible::remove);
                    visible.addAll(list);
                    show(list);
                } else {
                    list.forEach(visible::remove);
                    invisible.addAll(list);
                    hide(list);
                }
            });
        }
    }

    protected abstract void collect(BaseStages stages, BaseStages.CompileIndex compileIndex, Entry compiledEntry, Collector<Data> collector);

    protected abstract void registerUpdateNotifier(BaseStages stages, BaseStages.CompileIndex compileIndex, List<Entry> compiledEntries, UpdateRegistrar<Data> registrar);

    protected abstract void show(List<Data> show);

    protected abstract void hide(List<Data> hide);

    public interface UpdateRegistrar<Data> {
        void register(CompiledRestrictionPredicate predicate, Data data);

        void register(CompiledRestrictionPredicate predicate, Collection<? extends Data> data);
    }

    public record Collected<Data>(List<Data> showCache, List<Data> hideCache) {
        public Collected {
            showCache = List.copyOf(showCache);
            hideCache = List.copyOf(hideCache);
        }
    }

    public static final class Collector<Data> {
        private final List<Data> showCache = new ArrayList<>();
        private final List<Data> hideCache = new ArrayList<>();

        public void show(Data data) {
            showCache.add(data);
        }

        public void showAll(Collection<? extends Data> data) {
            showCache.addAll(data);
        }

        public void hide(Data data) {
            hideCache.add(data);
        }

        public void hideAll(Collection<? extends Data> data) {
            hideCache.addAll(data);
        }
    }
}
