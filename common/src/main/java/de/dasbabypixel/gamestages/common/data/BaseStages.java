package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionEntryCompiler;
import org.jspecify.annotations.NonNull;

import java.util.*;

public abstract class BaseStages {
    protected final @NonNull Map<@NonNull RestrictionEntry<?, ?>, @NonNull CompiledRestrictionEntry> compiledRestrictionEntryMap = new HashMap<>();
    protected final @NonNull Map<@NonNull GameStage, @NonNull CompiledRestrictionPredicate> compiledGameStages = new HashMap<>();
    protected final @NonNull Map<@NonNull GameContentType<?>, TypeIndex> typeIndexMap = new HashMap<>();
    private final Set<@NonNull GameStage> unlockedStages;

    public BaseStages(Set<GameStage> stages) {
        unlockedStages = new HashSet<>(stages);
    }

    /**
     * Invalidates the stage for recalculation
     */
    protected void update(@NonNull GameStage gameStage) {
        var compiled = compiledGameStages.get(gameStage);
        if (compiled != null) {
            compiled.invalidate();
        }
    }

    /**
     * Add a stage without notifying players
     */
    public boolean addSilent(@NonNull GameStage gameStage) {
        if (!getUnlockedStages().add(gameStage)) return false;
        onAdd(gameStage, true);
        return true;
    }

    /**
     * Remove a stage without notifying players
     */
    public boolean removeSilent(@NonNull GameStage gameStage) {
        if (!getUnlockedStages().add(gameStage)) return false;
        onRemove(gameStage, true);
        return true;
    }

    /**
     * Add a stage and notify players
     */
    public boolean add(@NonNull GameStage gameStage) {
        if (!getUnlockedStages().add(gameStage)) return false;
        onAdd(gameStage, false);
        return true;
    }

    /**
     * Remove a stage and notify players
     */
    public boolean remove(@NonNull GameStage gameStage) {
        if (!getUnlockedStages().remove(gameStage)) return false;
        onRemove(gameStage, false);
        return true;
    }

    public void recompileAll(@NonNull RestrictionEntryCompiler restrictionEntryCompiler) {
        var recompilationTask = new RecompilationTask(this, restrictionEntryCompiler, restrictionEntryCompiler.instance());
        recompilationTask.recompile();
        recompilationTask.findDuplicates();
        recompilationTask.firePostCompile();
    }

    protected void onAdd(@NonNull GameStage gameStage, boolean silent) {
        update(gameStage);
    }

    protected void onRemove(@NonNull GameStage gameStage, boolean silent) {
        update(gameStage);
    }

    protected Set<GameStage> getUnlockedStages() {
        return unlockedStages;
    }

    public boolean hasUnlocked(@NonNull GameStage gameStage) {
        return getUnlockedStages().contains(gameStage);
    }

    public @NonNull Set<@NonNull GameStage> getAll() {
        return Set.copyOf(getUnlockedStages());
    }

    public @NonNull Map<@NonNull GameStage, @NonNull CompiledRestrictionPredicate> compiledGameStages() {
        return compiledGameStages;
    }

    public @NonNull Map<@NonNull RestrictionEntry<?, ?>, @NonNull CompiledRestrictionEntry> compiledRestrictionEntryMap() {
        return compiledRestrictionEntryMap;
    }

    public @NonNull Map<@NonNull GameContentType<?>, TypeIndex> typeIndexMap() {
        return typeIndexMap;
    }

    public static final class TypeIndex {
        private final @NonNull Map<@NonNull Object, @NonNull CompiledRestrictionEntry> entryByContent = new HashMap<>();
        private final @NonNull Map<@NonNull CompiledRestrictionEntry, @NonNull List<@NonNull Object>> contentListByEntry = new HashMap<>();
        private final @NonNull Map<Object, Set<CompiledRestrictionEntry>> duplicates = new HashMap<>(0);

        public @NonNull Map<Object, Set<CompiledRestrictionEntry>> duplicates() {
            return duplicates;
        }

        public @NonNull Map<@NonNull Object, @NonNull CompiledRestrictionEntry> entryByContent() {
            return entryByContent;
        }

        public @NonNull Map<@NonNull CompiledRestrictionEntry, @NonNull List<@NonNull Object>> contentListByEntry() {
            return contentListByEntry;
        }
    }
}
