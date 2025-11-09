package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionEntryCompiler;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionPredicateCompiler;
import de.dasbabypixel.gamestages.common.data.restriction.types.RestrictionEntry;
import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class PlayerStages {
    private final @NonNull Player player;
    private final @NonNull Set<@NonNull GameStage> unlockedStages = new HashSet<>();
    private final @NonNull Map<@NonNull GameStage, @NonNull CompiledRestrictionPredicate> compiledGameStages = new HashMap<>();
    private final @NonNull Map<@NonNull RestrictionEntry<?, ?>, CompiledRestrictionEntry> compiledRestrictionEntryMap = new HashMap<>();

    public PlayerStages(@NonNull Player player) {
        this.player = player;
        unlockedStages.addAll(CommonInstances.platformPlayerStagesProvider.getStages(player));
    }

    public void recompileAll(@NonNull RestrictionEntryCompiler restrictionEntryCompiler) {
        var instance = restrictionEntryCompiler.instance();
        compiledGameStages.clear();
        compiledRestrictionEntryMap.clear();
        var compiler = new RestrictionPredicateCompiler(player);
        for (var gameStage : instance.gameStages()) {
            var compiled = compiler.compile(gameStage);
            compiledGameStages.put(gameStage, compiled);
        }
        for (var restriction : instance.restrictions()) {
            var predicate = restriction.predicate();
            // Compiling also links dependencies
            var compiledPredicate = compiler.compile(predicate);
            var compiledEntry = restrictionEntryCompiler.compile(player, restriction, compiledPredicate);

            compiledRestrictionEntryMap.put(restriction, compiledEntry);
        }
    }

    private void update(@NonNull GameStage gameStage) {
        var compiled = compiledGameStages.get(gameStage);
        if (compiled != null) {
            compiled.invalidate();
        }
    }

    public boolean addSilent(@NonNull GameStage gameStage) {
        if (!unlockedStages.add(gameStage)) return false;
        update(gameStage);
        CommonInstances.platformPlayerStagesProvider.setStages(player, unlockedStages);
        return true;
    }

    public boolean removeSilent(@NonNull GameStage gameStage) {
        if (!unlockedStages.add(gameStage)) return false;
        update(gameStage);
        CommonInstances.platformPlayerStagesProvider.setStages(player, unlockedStages);
        return true;
    }

    public boolean add(@NonNull GameStage gameStage) {
        if (!unlockedStages.add(gameStage)) return false;
        update(gameStage);
        CommonInstances.platformPlayerStagesProvider.setStages(player, unlockedStages);
        fullSync();
        return true;
    }

    public boolean remove(@NonNull GameStage gameStage) {
        if (!unlockedStages.remove(gameStage)) return false;
        update(gameStage);
        CommonInstances.platformPlayerStagesProvider.setStages(player, unlockedStages);
        fullSync();
        return true;
    }

    public void syncUnlockedStages(List<GameStage> gameStages) {
        var updated = new HashSet<GameStage>();
        var keep = Set.copyOf(gameStages);
        var it = unlockedStages.iterator();
        while (it.hasNext()) {
            var stage = it.next();
            if (keep.contains(stage)) continue;
            it.remove();
            updated.add(stage);
        }
        for (var stage : gameStages) {
            if (unlockedStages.add(stage)) {
                updated.add(stage);
            }
        }
        updated.forEach(this::update);
        compiledGameStages.values().forEach(CompiledRestrictionPredicate::test);
    }

    public void fullSync() {
        CommonInstances.platformPacketDistributor.sendToPlayer(player, CommonInstances.platformPacketCreator.createSyncUnlockedGameStages(List.copyOf(unlockedStages)));
    }

    public boolean hasUnlocked(@NonNull GameStage gameStage) {
        return unlockedStages.contains(gameStage);
    }

    public @NonNull Set<@NonNull GameStage> getAll() {
        return Set.copyOf(unlockedStages);
    }

    public @NonNull Player getPlayer() {
        return player;
    }
}
