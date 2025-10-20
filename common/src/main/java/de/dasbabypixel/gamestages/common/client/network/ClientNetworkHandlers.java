package de.dasbabypixel.gamestages.common.client.network;

import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.data.GameStage;

import java.util.List;

public class ClientNetworkHandlers {
    /**
     * Updates the list of registered GameStages on the client-side.
     * This does not give the stages to the player
     */
    public static void syncRegisteredGameStages(List<GameStage> gameStages) {
        System.out.println("Handle sync game stages");
        System.out.println("Handle sync game stages");
        System.out.println(gameStages);
        ClientGameStageManager.INSTANCE.set(gameStages);
        System.out.println("post Handle sync game stages");
        System.out.println("post Handle sync game stages");
    }
}
