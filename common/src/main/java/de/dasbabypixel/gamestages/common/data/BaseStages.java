package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.data.attribute.AbstractAttributeHolder;
import de.dasbabypixel.gamestages.common.data.attribute.Attribute;
import de.dasbabypixel.gamestages.common.data.manager.immutable.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@NullMarked
public class BaseStages extends AbstractAttributeHolder<BaseStages> {
    private final Set<GameStage> unlockedStages;

    public BaseStages(Collection<GameStage> stages) {
        unlockedStages = new HashSet<>(stages);
    }

    /**
     * Invalidates the stage for recalculation
     */
    protected void update(GameStage gameStage) {
        var compileIndex = get(CompileIndex.ATTRIBUTE);
        var compiled = compileIndex.compiledGameStages.get(gameStage);
        if (compiled != null) {
            compiled.invalidate();
        }
    }

    /**
     * Add a stage without notifying players
     */
    public boolean addSilent(GameStage gameStage) {
        if (!getUnlockedStages().add(gameStage)) return false;
        onAdd(gameStage, true);
        return true;
    }

    /**
     * Remove a stage without notifying players
     */
    public boolean removeSilent(GameStage gameStage) {
        if (!getUnlockedStages().add(gameStage)) return false;
        onRemove(gameStage, true);
        return true;
    }

    /**
     * Add a stage and notify players
     */
    public boolean add(GameStage gameStage) {
        if (!getUnlockedStages().add(gameStage)) return false;
        onAdd(gameStage, false);
        return true;
    }

    /**
     * Remove a stage and notify players
     */
    public boolean remove(GameStage gameStage) {
        if (!getUnlockedStages().remove(gameStage)) return false;
        onRemove(gameStage, false);
        return true;
    }

    public void recompileAll(AbstractGameStageManager<?> manager) {
        var recompilationTask = new PlayerCompilationTask(this, manager);
        recompilationTask.compile();
    }

    protected void onAdd(GameStage gameStage, boolean silent) {
        update(gameStage);
    }

    protected void onRemove(GameStage gameStage, boolean silent) {
        update(gameStage);
    }

    public Set<GameStage> getUnlockedStages() {
        return unlockedStages;
    }

    public boolean hasUnlocked(GameStage gameStage) {
        return getUnlockedStages().contains(gameStage);
    }

    public Set<GameStage> getAll() {
        return Objects.requireNonNull(Set.copyOf(getUnlockedStages()));
    }

    public static final class CompileIndex {
        public static final Attribute<BaseStages, CompileIndex> ATTRIBUTE = new Attribute<>(CompileIndex::new);
        private final Map<GameContentType<?>, TypeIndex> typeIndexMap = new HashMap<>();
        private final List<CompiledRestrictionEntry<?, ?>> compiledRestrictionEntries = new ArrayList<>();
        private final Map<GameStage, CompiledRestrictionPredicate> compiledGameStages = new HashMap<>();

        public TypeIndex typeIndex(GameContentType<?> type) {
            return Objects.requireNonNull(typeIndexMap.get(type));
        }

        public void initTypeIndex(TypeIndex typeIndex) {
            if (typeIndexMap.containsKey(typeIndex.type)) throw new IllegalStateException();
            typeIndexMap.put(typeIndex.type, typeIndex);
        }

        public void initGameStages(Map<GameStage, CompiledRestrictionPredicate> gameStageMap) {
            if (!compiledGameStages.isEmpty()) throw new IllegalStateException();
            compiledGameStages.putAll(gameStageMap);
        }

        public void initCompiledRestrictionEntries(List<CompiledRestrictionEntry<?, ?>> entries) {
            if (!compiledRestrictionEntries.isEmpty()) throw new IllegalStateException();
            compiledRestrictionEntries.addAll(entries);
        }

        public void clear() {
            typeIndexMap.clear();
            compiledRestrictionEntries.clear();
            compiledGameStages.clear();
        }
    }

    public record TypeIndex(GameContentType<?> type,
                            Map<CompiledRestrictionEntry<?, ?>, List<Object>> contentListByEntry,
                            Map<Object, CompiledRestrictionEntry<?, ?>> entryByContent) {
        public TypeIndex {
            contentListByEntry = Map.copyOf(contentListByEntry);
            entryByContent = Map.copyOf(entryByContent);
        }
    }

    public static final class MutableTypeIndex {
        private final GameContentType<?> type;
        private final Map<CompiledRestrictionEntry<?, ?>, List<Object>> contentListByEntry = new HashMap<>();
        private final Map<Object, CompiledRestrictionEntry<?, ?>> entryByContent = new HashMap<>();

        public MutableTypeIndex(GameContentType<?> type) {
            this.type = type;
        }

        public Map<CompiledRestrictionEntry<?, ?>, List<Object>> contentListByEntry() {
            return contentListByEntry;
        }

        public Map<Object, CompiledRestrictionEntry<?, ?>> entryByContent() {
            return entryByContent;
        }

        public TypeIndex compile() {
            return new TypeIndex(type, contentListByEntry, entryByContent);
        }
    }
}
