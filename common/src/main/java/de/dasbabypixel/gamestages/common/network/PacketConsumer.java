package de.dasbabypixel.gamestages.common.network;

import org.jspecify.annotations.NonNull;

public interface PacketConsumer {
    void send(@NonNull CustomPacket packet);
}
