package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import org.jspecify.annotations.NullMarked;

import java.util.*;

@NullMarked
public abstract class BaseStages {
    protected final Map<RestrictionEntry<?, ?>, CompiledRestrictionEntry> compiledRestrictionEntryMap = new HashMap<>();
    protected final Map<GameStage, CompiledRestrictionPredicate> compiledGameStages = new HashMap<>();
    protected final Map<GameContentType<?>, TypeIndex> typeIndexMap = new HashMap<>();
    protected final Map<Addon, Object> addonData = new HashMap<>();
    private final Set<GameStage> unlockedStages;

    public BaseStages(Set<GameStage> stages) {
        unlockedStages = new HashSet<>(stages);
    }

    /**
     * Invalidates the stage for recalculation
     */
    protected void update(GameStage gameStage) {
        var compiled = compiledGameStages.get(gameStage);
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

    public void recompileAll(AbstractGameStageManager<?> instance) {
        var recompilationTask = new RecompilationTask(this, instance);
        recompilationTask.recompile();
        recompilationTask.findDuplicates();
        recompilationTask.firePostCompile();
    }

    protected void onAdd(GameStage gameStage, boolean silent) {
        update(gameStage);
    }

    protected void onRemove(GameStage gameStage, boolean silent) {
        update(gameStage);
    }

    protected Set<GameStage> getUnlockedStages() {
        return unlockedStages;
    }

    public boolean hasUnlocked(GameStage gameStage) {
        return getUnlockedStages().contains(gameStage);
    }

    public Set<GameStage> getAll() {
        return Objects.requireNonNull(Set.copyOf(getUnlockedStages()));
    }

    public Map<GameStage, CompiledRestrictionPredicate> compiledGameStages() {
        return compiledGameStages;
    }

    public Map<RestrictionEntry<?, ?>, CompiledRestrictionEntry> compiledRestrictionEntryMap() {
        return compiledRestrictionEntryMap;
    }

    public Map<Addon, Object> addonData() {
        return addonData;
    }

    public Map<GameContentType<?>, TypeIndex> typeIndexMap() {
        return typeIndexMap;
    }

    public static final class TypeIndex {
        private final Map<Object, CompiledRestrictionEntry> entryByContent = new HashMap<>();
        private final Map<CompiledRestrictionEntry, List<Object>> contentListByEntry = new HashMap<>();
        private final Map<Object, Set<CompiledRestrictionEntry>> duplicates = new HashMap<>(0);

        public Map<Object, Set<CompiledRestrictionEntry>> duplicates() {
            return duplicates;
        }

        public Map<Object, CompiledRestrictionEntry> entryByContent() {
            return entryByContent;
        }

        public Map<CompiledRestrictionEntry, List<Object>> contentListByEntry() {
            return contentListByEntry;
        }
    }
}
