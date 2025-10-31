package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerStages {
    private final Player player;
    private final Set<GameStage> unlockedStages = new HashSet<>();

    public PlayerStages(@NonNull Player player) {
        this.player = player;
        unlockedStages.addAll(CommonInstances.platformPlayerStagesProvider.getStages(player));
    }

    public boolean addSilent(@NonNull GameStage gameStage) {
        if (!unlockedStages.add(gameStage)) return false;
        CommonInstances.platformPlayerStagesProvider.setStages(player, unlockedStages);
        return true;
    }

    public boolean removeSilent(@NonNull GameStage gameStage) {
        if (!unlockedStages.add(gameStage)) return false;
        CommonInstances.platformPlayerStagesProvider.setStages(player, unlockedStages);
        return true;
    }

    public boolean add(@NonNull GameStage gameStage) {
        if (!unlockedStages.add(gameStage)) return false;
        CommonInstances.platformPlayerStagesProvider.setStages(player, unlockedStages);
        fullSync();
        return true;
    }

    public boolean remove(@NonNull GameStage gameStage) {
        if (!unlockedStages.remove(gameStage)) return false;
        CommonInstances.platformPlayerStagesProvider.setStages(player, unlockedStages);
        fullSync();
        return true;
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
