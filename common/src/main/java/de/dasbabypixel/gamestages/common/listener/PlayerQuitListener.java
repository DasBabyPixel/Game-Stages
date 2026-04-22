package de.dasbabypixel.gamestages.common.listener;

import de.dasbabypixel.gamestages.common.data.server.GlobalServerState;
import de.dasbabypixel.gamestages.common.entity.ServerPlayer;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PlayerQuitListener {
    public static void handleQuit(ServerPlayer player) {
        GlobalServerState.state().stagesCache().release(player.getGameStages());
    }
}
