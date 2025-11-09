package de.dasbabypixel.gamestages.common.client.network;

import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
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
        ClientGameStageManager.INSTANCE.set(gameStages);
    }

    public static void syncUnlockedGameStages(List<GameStage> gameStages) {
    }

    public static void itemRestriction(@NonNull PreparedRestrictionPredicate predicate, @NonNull ItemCollection<?> targetCollection, boolean hideTooltip, boolean renderItemName, boolean hideInJEI) {
    }

    public static void status(Status status) {
        System.out.println("client status " + status);
    }
}
