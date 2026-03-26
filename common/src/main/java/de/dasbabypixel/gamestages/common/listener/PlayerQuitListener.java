package de.dasbabypixel.gamestages.common.listener;

import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.entity.ServerPlayer;

public class PlayerQuitListener {
    public static void handleQuit(ServerPlayer player) {
        ((ServerGameStageManager) ServerGameStageManager.instance())
                .playerStagesCache()
                .release(player.getGameStages());
    }
}
