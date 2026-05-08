package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.data.attribute.ImmutableAttribute;
import de.dasbabypixel.gamestages.common.data.attribute.ReplaceableImmutableAttributeHolder;
import de.dasbabypixel.gamestages.common.data.attribute.SimpleImmutableAttribute;
import de.dasbabypixel.gamestages.common.data.manager.immutable.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@NullMarked
public class BaseStages extends ReplaceableImmutableAttributeHolder<BaseStages> {
    private static final Logger LOGGER = Objects.requireNonNull(LoggerFactory.getLogger(BaseStages.class));
    private final Set<GameStage> unlockedStages;

    public BaseStages(Collection<? extends GameStage> stages) {
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
        var time1 = System.nanoTime();
        var recompilationTask = new PlayerCompilationTask(this, manager);
        recompilationTask.compile();
        var took = System.nanoTime() - time1;
        LOGGER.info("Compiling GameStages for player took {}ms", TimeUnit.NANOSECONDS.toMillis(took));
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

    public record CompileIndex(Map<GameStage, CompiledRestrictionPredicate> compiledGameStages,
                               Map<GameContentType<?>, TypeIndex> typeIndexMap,
                               List<CompiledRestrictionEntry<?, ?>> compiledRestrictionEntries) {
        public static final ImmutableAttribute<BaseStages, CompileIndex> ATTRIBUTE = new SimpleImmutableAttribute<>();

        public CompileIndex {
            compiledGameStages = Objects.requireNonNull(Map.copyOf(compiledGameStages));
            typeIndexMap = Objects.requireNonNull(Map.copyOf(typeIndexMap));
            compiledRestrictionEntries = Objects.requireNonNull(List.copyOf(compiledRestrictionEntries));
        }

        public TypeIndex typeIndex(GameContentType<?> type) {
            return Objects.requireNonNull(typeIndexMap.get(type));
        }
    }

    public record TypeIndex(GameContentType<?> type,
                            Map<CompiledRestrictionEntry<?, ?>, List<Object>> contentListByEntry,
                            Map<Object, CompiledRestrictionEntry<?, ?>> entryByContent) {
        public TypeIndex {
            contentListByEntry = Objects.requireNonNull(Map.copyOf(contentListByEntry));
            entryByContent = Objects.requireNonNull(Map.copyOf(entryByContent));
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
