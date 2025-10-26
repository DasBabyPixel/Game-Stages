package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ServerGameStageManager extends MutatableGameStageManager {
    public static @Nullable ServerGameStageManager INSTANCE;
    private static boolean queuing = false;

    public static void stop() {
        Objects.requireNonNull(INSTANCE).disallowMutation();
        INSTANCE = null;
    }

    public static void init() {
        if (INSTANCE != null) throw new IllegalStateException("Instance not null");
        INSTANCE = new ServerGameStageManager();
        INSTANCE.disallowMutation();
        if (queuing) {
            QueuingGameStageManager.INSTANCE.end(INSTANCE);
            queuing = false;
        }
    }

    public static @NonNull MutatableGameStageManager instance() {
        if (INSTANCE != null) {
            if (queuing) {
                QueuingGameStageManager.INSTANCE.end(INSTANCE);
                queuing = false;
            }
            return INSTANCE;
        }
        if (!queuing) {
            queuing = true;
            QueuingGameStageManager.INSTANCE.begin();
        }
        return QueuingGameStageManager.INSTANCE;
    }

    public void sync() {
        var stages = List.copyOf(this.getGameStages().values());
        var packet = CommonInstances.platformPacketCreator.createSyncRegisteredGameStages(stages);
        CommonInstances.platformPacketDistributor.sendToAllPlayers(packet);
    }

    public void sync(Player target) {
        var stages = List.copyOf(this.getGameStages().values());
        var packet = CommonInstances.platformPacketCreator.createSyncRegisteredGameStages(stages);
        CommonInstances.platformPacketDistributor.sendToPlayer(target, packet);
    }
}
