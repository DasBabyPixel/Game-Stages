package de.dasbabypixel.gamestages.common.client;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ClientGameStageManager extends AbstractGameStageManager<ClientGameStageManager> {
    private static @Nullable ClientGameStageManager INSTANCE;

    private ClientGameStageManager() {
    }

    public @Nullable Player player() {
        return CommonInstances.platformPlayerProvider.clientSelfPlayer();
    }

    public static ClientGameStageManager instance() {
        if (INSTANCE == null) INSTANCE = new ClientGameStageManager();
        return INSTANCE;
    }
}
