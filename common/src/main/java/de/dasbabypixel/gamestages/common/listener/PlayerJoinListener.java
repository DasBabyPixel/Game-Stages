package de.dasbabypixel.gamestages.common.listener;

import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.entity.Player;

import java.util.Objects;

public class PlayerJoinListener {
    public static void handleJoin(Player player) {
        var instance = Objects.requireNonNull(ServerGameStageManager.INSTANCE);
        instance.sync(player);
    }
}
