package de.dasbabypixel.gamestages.common.v1_21_1.network;

import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.CommonFluidRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.CommonItemRestrictionPacket;

public interface PlatformPacketHandler {
    void handle(CommonItemRestrictionPacket packet);

    void handle(CommonFluidRestrictionPacket packet);
}
