package de.dasbabypixel.gamestages.common.network;

import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.NonNull;

public interface PlatformPacketDistributor {
    void sendToServer(@NonNull CustomPacket packet);

    void sendToPlayer(@NonNull Player player, @NonNull CustomPacket packet);

    void sendToAllPlayers(@NonNull CustomPacket packet);
}
