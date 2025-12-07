package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionEntryCompiler;
import de.dasbabypixel.gamestages.common.data.restriction.types.RestrictionEntry;
import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class PlayerStages {
    private final @NonNull Map<@NonNull RestrictionEntry<?, ?>, @NonNull CompiledRestrictionEntry> compiledRestrictionEntryMap = new HashMap<>();
    private final @NonNull Map<@NonNull GameStage, @NonNull CompiledRestrictionPredicate> compiledGameStages = new HashMap<>();
    private final @NonNull Map<@NonNull GameContentType<?>, TypeIndex> typeIndexMap = new HashMap<>();
    private final @NonNull Player player;
    private @Nullable Set<@NonNull GameStage> unlockedStages;

    public PlayerStages(@NonNull Player player) {
        this.player = player;
    }

    private Set<GameStage> unlockedStages() {
        if (unlockedStages == null) {
            unlockedStages = new HashSet<>(CommonInstances.platformPlayerStagesProvider.getStages(player));
        }
        return unlockedStages;
    }

    public void recompileAll(@NonNull RestrictionEntryCompiler restrictionEntryCompiler) {
        var recompilationTask = new RecompilationTask(this, restrictionEntryCompiler, restrictionEntryCompiler.instance(), player);
        recompilationTask.recompile();
        recompilationTask.findDuplicates();
        recompilationTask.firePostCompile();
    }

    private void update(@NonNull GameStage gameStage) {
        var compiled = compiledGameStages.get(gameStage);
        if (compiled != null) {
            compiled.invalidate();
        }
    }

    public boolean addSilent(@NonNull GameStage gameStage) {
        if (!unlockedStages().add(gameStage)) return false;
        update(gameStage);
        CommonInstances.platformPlayerStagesProvider.setStages(player, unlockedStages());
        return true;
    }

    public boolean removeSilent(@NonNull GameStage gameStage) {
        if (!unlockedStages().add(gameStage)) return false;
        update(gameStage);
        CommonInstances.platformPlayerStagesProvider.setStages(player, unlockedStages());
        return true;
    }

    public boolean add(@NonNull GameStage gameStage) {
        if (!unlockedStages().add(gameStage)) return false;
        update(gameStage);
        CommonInstances.platformPlayerStagesProvider.setStages(player, unlockedStages());
        fullSync();
        return true;
    }

    public boolean remove(@NonNull GameStage gameStage) {
        if (!unlockedStages().remove(gameStage)) return false;
        update(gameStage);
        CommonInstances.platformPlayerStagesProvider.setStages(player, unlockedStages());
        fullSync();
        return true;
    }

    public void syncUnlockedStages(List<GameStage> gameStages) {
        var updated = new HashSet<GameStage>();
        var keep = Set.copyOf(gameStages);
        var it = unlockedStages().iterator();
        while (it.hasNext()) {
            var stage = it.next();
            if (keep.contains(stage)) continue;
            it.remove();
            updated.add(stage);
        }
        for (var stage : gameStages) {
            if (unlockedStages().add(stage)) {
                updated.add(stage);
            }
        }
        updated.forEach(this::update);
        compiledGameStages.values().forEach(CompiledRestrictionPredicate::test);
        for (var addon : ClientGameStageManager.instance().addons()) {
            addon.clientPostSyncUnlockedStages(this);
        }
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

    public void fullSync() {
        CommonInstances.platformPacketDistributor.sendToPlayer(player, CommonInstances.platformPacketCreator.createSyncUnlockedGameStages(List.copyOf(unlockedStages())));
    }

    public boolean hasUnlocked(@NonNull GameStage gameStage) {
        return unlockedStages().contains(gameStage);
    }

    public @NonNull Set<@NonNull GameStage> getAll() {
        return Set.copyOf(unlockedStages());
    }

    public @NonNull Player getPlayer() {
        return player;
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
