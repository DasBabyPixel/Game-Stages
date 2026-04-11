package de.dasbabypixel.gamestages.common.network;

import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PlatformPacketDistributor {
    void sendToServer(CustomPacket packet);

    void sendToPlayer(Player player, CustomPacket packet);

    void sendToAllPlayers(CustomPacket packet);
}
