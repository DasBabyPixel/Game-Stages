package de.dasbabypixel.gamestages.common.client.network;

import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.network.Status;

import java.util.List;

public class ClientNetworkHandlers {
    /**
     * Updates the list of registered GameStages on the client-side.
     * This does not give any stages to the player
     */
    public static void syncRegisteredGameStages(List<GameStage> gameStages) {
        ClientGameStageManager.instance().set(gameStages);
    }

    public static void syncUnlockedGameStages(List<GameStage> gameStages) {
    }

    public static void status(Status status) {
        System.out.println("client status " + status);
    }
}
