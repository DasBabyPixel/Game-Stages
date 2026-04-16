package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.addon.Addon.PreCompilePrepareEvent;
import de.dasbabypixel.gamestages.common.data.attribute.AbstractAttributeHolder;
import de.dasbabypixel.gamestages.common.data.attribute.Attribute;
import de.dasbabypixel.gamestages.common.data.restriction.DuplicateReport;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static de.dasbabypixel.gamestages.common.addon.Addon.PRE_COMPILE_PREPARE_EVENT;

@NullMarked
public abstract class AbstractGameStageManager<H extends AbstractGameStageManager<H>> extends AbstractAttributeHolder<H> {
    protected final Set<GameStage> gameStages = new HashSet<>();
    protected final List<RestrictionEntry<?, ?, ?>> restrictions = new ArrayList<>();

    public void add(GameStage gameStage) {
        if (!mayMutate()) throw new IllegalStateException("Cannot mutate");
        if (containsKey(gameStage)) {
            throw new IllegalArgumentException("Multiple GameStages have the same name");
        }
        add0(gameStage);
    }

    public @Nullable GameStage get(String name) {
        var stage = new GameStage(name);
        if (!containsKey(stage)) return null;
        return stage;
    }

    public <T extends RestrictionEntry<T, ?, ?>> T addRestriction(T restriction) {
        this.restrictions.add(restriction);
        return restriction;
    }

    public void set(List<GameStage> gameStages) {
        reset();
        gameStages.forEach(this::add);
    }

    public void preparePrecompileRestrictions() {
        PRE_COMPILE_PREPARE_EVENT.call(new PreCompilePrepareEvent(this));
        var preCompileIndex = get(PreCompileIndex.ATTRIBUTE);
        preCompileIndex.clear();
    }

    public void precompileRestrictions() {
        var preCompileIndex = get(PreCompileIndex.ATTRIBUTE);

        var duplicates = new HashMap<GameContentType<?>, Map<Object, Set<RestrictionEntry.PreCompiled<?, ?>>>>(0);

        for (var restriction : restrictions()) {
            var preCompiled = restriction.precompile(this);
            var typed = restriction.gameContent();
            var type = typed.type();
            preCompileIndex.entries.add(preCompiled);

            if (typed.isEmpty()) continue;
            var typeIndex = preCompileIndex.typeIndex(type);

            typeIndex.entries.add(preCompiled);

            for (var content : typed.content()) {
                if (typeIndex.preCompiledByContent.containsKey(content)) {
                    duplicates.computeIfAbsent(type, ignored -> new HashMap<>())
                            .computeIfAbsent(content, ignored -> new HashSet<>())
                            .add(preCompiled);
                } else {
                    typeIndex.preCompiledByContent.put(content, preCompiled);
                }
            }
        }

        var reports = collectDuplicateReports(preCompileIndex.typeIndexMap, duplicates);

        if (!reports.isEmpty()) {
            preCompileIndex.clear();
            throw new DuplicatesException(reports);
        }
    }

    private List<DuplicateReport> collectDuplicateReports(Map<GameContentType<?>, TypeIndex<?>> typeIndexMap, Map<GameContentType<?>, Map<Object, Set<RestrictionEntry.PreCompiled<?, ?>>>> duplicatesByType) {
        if (duplicatesByType.isEmpty()) return List.of();
        var reports = new ArrayList<DuplicateReport>();
        for (var entry : duplicatesByType.entrySet()) {
            assert entry != null;
            var typeIndex = Objects.requireNonNull(typeIndexMap.get(entry.getKey()));
            var duplicates = entry.getValue();
            for (var duplicateEntry : duplicates.entrySet()) {
                assert duplicateEntry != null;
                var content = duplicateEntry.getKey();
                var mainEntry = Objects.requireNonNull(typeIndex.preCompiledByContent.get(content));
                var otherDuplicates = duplicateEntry.getValue();
                reports.add(new DuplicateReport(content, mainEntry, otherDuplicates));
            }
        }
        return reports;
    }

    public void reset() {
        clear0();
    }

    public Set<GameStage> gameStages() {
        return gameStages;
    }

    public List<RestrictionEntry<?, ?, ?>> restrictions() {
        return restrictions;
    }

    protected void clear0() {
        gameStages.clear();
        restrictions.clear();
        attributeMap.clear();
    }

    protected boolean containsKey(GameStage gameStage) {
        return gameStages.contains(gameStage);
    }

    protected void add0(GameStage stage) {
        gameStages.add(stage);
    }

    protected boolean mayMutate() {
        return true;
    }

    public static final class PreCompileIndex {
        public static final Attribute<AbstractGameStageManager<?>, PreCompileIndex> ATTRIBUTE = new Attribute<>(PreCompileIndex::new);

        private final Set<RestrictionEntry.PreCompiled<?, ?>> entries = new HashSet<>();
        private final Map<GameContentType<?>, TypeIndex<?>> typeIndexMap = new HashMap<>();

        public Set<RestrictionEntry.PreCompiled<?, ?>> preCompiledRestrictions() {
            return entries;
        }

        @SuppressWarnings("unchecked")
        public <Type extends TypedGameContent> TypeIndex<Type> typeIndex(GameContentType<Type> type) {
            return (TypeIndex<Type>) typeIndexMap.computeIfAbsent(type, ignored -> new TypeIndex<>());
        }

        private void clear() {
            entries.clear();
            typeIndexMap.clear();
        }
    }

    public static final class TypeIndex<Type extends TypedGameContent> {
        private final Set<RestrictionEntry.PreCompiled<?, ?>> entries = new HashSet<>();
        private final Map<Object, RestrictionEntry.PreCompiled<?, ?>> preCompiledByContent = new HashMap<>();

        @SuppressWarnings("unchecked")
        public <PC extends RestrictionEntry.PreCompiled<?, ?>> Set<PC> entries() {
            return (Set<PC>) entries;
        }
    }
}
