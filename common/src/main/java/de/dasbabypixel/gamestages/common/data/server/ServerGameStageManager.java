package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ServerGameStageManager extends AbstractGameStageManager {
    @Nullable
    public static ServerGameStageManager INSTANCE = null;
    private boolean allowMutation = false;

    public static void stop() {
        INSTANCE = null;
    }

    public static void init() {
        INSTANCE = new ServerGameStageManager();
    }

    public void sync() {
        var stages = List.copyOf(this.getGameStages().values());
        var packet = CommonInstances.platformPacketCreator.createSyncRegisteredGameStages(stages);
        CommonInstances.platformPacketDistributor.sendToAllPlayers(packet);
    }

    @Override
    protected boolean mayMutate() {
        return allowMutation;
    }

    public void allowMutation() {
        allowMutation = true;
    }

    public void disallowMutation() {
        allowMutation = false;
    }
}
