package de.dasbabypixel.gamestages.common.client.network;

import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.ItemCollection;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.network.Status;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ClientNetworkHandlers {
    /**
     * Updates the list of registered GameStages on the client-side.
     * This does not give any stages to the player
     */
    public static void syncRegisteredGameStages(List<GameStage> gameStages) {
        System.out.println("Client registered stages: " + gameStages);
//        System.out.println("Handle sync game stages");
//        System.out.println("Handle sync game stages");
//        System.out.println(gameStages);
//        ClientGameStageManager.INSTANCE.set(gameStages);
//        ClientGameStageManager.INSTANCE.gameStages().forEach((reference, gameStage) -> {
//            System.out.println(gameStage);
//        });
//        System.out.println("post Handle sync game stages");
//        System.out.println("post Handle sync game stages");
    }

    public static void syncUnlockedGameStages(List<GameStage> gameStages) {
        System.out.println("client unlocked stages: " + gameStages);
    }

    public static void itemRestriction(@NonNull PreparedRestrictionPredicate predicate, @NonNull ItemCollection<?> targetCollection, boolean hideTooltip, boolean renderItemName, boolean hideInJEI) {
        
    }

    public static void status(Status status) {
//        System.out.println("Handle status " + status);
    }
}
