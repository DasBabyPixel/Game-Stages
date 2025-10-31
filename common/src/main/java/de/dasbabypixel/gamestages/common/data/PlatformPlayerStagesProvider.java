package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.entity.Player;

import java.util.Set;

public interface PlatformPlayerStagesProvider {
    void setStages(Player player, Set<GameStage> unlockedStages);

    Set<GameStage> getStages(Player player);
}
