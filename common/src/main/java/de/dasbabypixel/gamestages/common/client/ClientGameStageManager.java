package de.dasbabypixel.gamestages.common.client;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.Nullable;

public class ClientGameStageManager extends AbstractGameStageManager {
    private static ClientGameStageManager INSTANCE;

    private ClientGameStageManager() {
    }

    public static ClientGameStageManager instance() {
        if (INSTANCE == null) INSTANCE = new ClientGameStageManager();
        return INSTANCE;
    }

    public @Nullable Player player() {
        return CommonInstances.platformPlayerProvider.clientSelfPlayer();
    }
}
