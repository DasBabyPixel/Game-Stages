package de.dasbabypixel.gamestages.common.client.network;

import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.manager.mutable.ClientMutableGameStageManager;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class ClientNetworkHandlers {
    /**
     * Updates the list of registered GameStages on the client-side.
     * This does not give any stages to the player
     */
    public static void syncRegisteredGameStages(List<GameStage> gameStages) {
        ClientMutableGameStageManager.buildingInstance().addAll(gameStages);
    }
}
