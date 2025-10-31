package de.dasbabypixel.gamestages.common.listener;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.entity.Player;
import de.dasbabypixel.gamestages.common.network.PacketConsumer;

import java.util.Objects;

public class PlayerJoinListener {
    public static void handleJoin(Player player) {
        var instance = Objects.requireNonNull(ServerGameStageManager.INSTANCE);
        PacketConsumer packetConsumer = packet -> CommonInstances.platformPacketDistributor.sendToPlayer(player, packet);
        instance.sync(packetConsumer);
    }
}
