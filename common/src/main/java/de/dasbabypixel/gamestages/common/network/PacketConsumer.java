package de.dasbabypixel.gamestages.common.network;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PacketConsumer {
    void send(CustomPacket packet);
}
