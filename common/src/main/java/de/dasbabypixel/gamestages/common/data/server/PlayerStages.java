package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.GameStageReference;
import de.dasbabypixel.gamestages.common.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class PlayerStages {
    private final Player player;
    private final Set<GameStageReference> unlockedStages = new HashSet<>();

    public PlayerStages(Player player) {
        this.player = player;
        unlockedStages.addAll(CommonInstances.platformPlayerStagesProvider.getStages(player));
    }

    public boolean addSilent(GameStageReference reference) {
        if (!unlockedStages.add(reference)) return false;
        CommonInstances.platformPlayerStagesProvider.setStages(player, unlockedStages);
        return true;
    }

    public boolean removeSilent(GameStageReference reference) {
        if (!unlockedStages.add(reference)) return false;
        CommonInstances.platformPlayerStagesProvider.setStages(player, unlockedStages);
        return true;
    }

    public boolean add(GameStageReference reference) {
        if (!unlockedStages.add(reference)) return false;
        CommonInstances.platformPlayerStagesProvider.setStages(player, unlockedStages);
        sync();
        return true;
    }

    public boolean remove(GameStageReference reference) {
        if (!unlockedStages.remove(reference)) return false;
        CommonInstances.platformPlayerStagesProvider.setStages(player, unlockedStages);
        sync();
        return true;
    }

    public void sync() {
        CommonInstances.platformPlayerStagesProvider.setStages(player, unlockedStages);
    }

    public Set<GameStageReference> getAll() {
        return Set.copyOf(unlockedStages);
    }

    public Player getPlayer() {
        return player;
    }
}
