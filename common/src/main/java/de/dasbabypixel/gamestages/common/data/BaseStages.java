package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.data.attribute.AbstractAttributeHolder;
import de.dasbabypixel.gamestages.common.data.attribute.Attribute;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import org.jspecify.annotations.NullMarked;

import java.util.*;

@NullMarked
public abstract class BaseStages extends AbstractAttributeHolder<BaseStages> {
    private final Set<GameStage> unlockedStages;

    public BaseStages(Set<GameStage> stages) {
        unlockedStages = new HashSet<>(stages);
    }

    /**
     * Invalidates the stage for recalculation
     */
    protected void update(GameStage gameStage) {
        var compileIndex = get(CompileIndex.ATTRIBUTE);
        var compiled = compileIndex.compiledGameStages().get(gameStage);
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

    public static final class CompileIndex {
        public static final Attribute<BaseStages, CompileIndex> ATTRIBUTE = new Attribute<>(CompileIndex::new);
        private final Map<GameContentType<?>, TypeIndex> typeIndexMap = new HashMap<>();
        private final List<CompiledRestrictionEntry<?, ?>> compiledRestrictionEntries = new ArrayList<>();
        private final Map<GameStage, CompiledRestrictionPredicate> compiledGameStages = new HashMap<>();

        public Map<GameContentType<?>, TypeIndex> typeIndexMap() {
            return typeIndexMap;
        }

        public List<CompiledRestrictionEntry<?, ?>> compiledRestrictionEntries() {
            return compiledRestrictionEntries;
        }

        public Map<GameStage, CompiledRestrictionPredicate> compiledGameStages() {
            return compiledGameStages;
        }

        public void clear() {
            typeIndexMap.clear();
            compiledRestrictionEntries.clear();
            compiledGameStages.clear();
        }
    }

    public static final class TypeIndex {
        private final Map<CompiledRestrictionEntry<?, ?>, List<Object>> contentListByEntry = new HashMap<>();

        public Map<CompiledRestrictionEntry<?, ?>, List<Object>> contentListByEntry() {
            return contentListByEntry;
        }
    }
}
