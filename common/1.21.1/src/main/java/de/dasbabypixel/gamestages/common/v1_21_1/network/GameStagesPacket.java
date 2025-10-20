package de.dasbabypixel.gamestages.common.v1_21_1.network;

import de.dasbabypixel.gamestages.common.network.CustomPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface GameStagesPacket extends CustomPacketPayload, CustomPacket {
    void handle();
}
