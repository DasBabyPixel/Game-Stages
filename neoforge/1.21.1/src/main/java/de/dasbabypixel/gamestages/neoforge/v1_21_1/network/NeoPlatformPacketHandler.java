package de.dasbabypixel.gamestages.neoforge.v1_21_1.network;

import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.network.PlatformPacketHandler;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.CommonItemRestrictionPacket;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.restriction.types.NeoItemRestrictionEntry;

public class NeoPlatformPacketHandler implements PlatformPacketHandler {
    @Override
    public void handle(CommonItemRestrictionPacket packet) {
        System.out.println("Add client restriciton");
        ClientGameStageManager.INSTANCE.addRestriction(new NeoItemRestrictionEntry(packet.predicate(), packet.targetCollection()));
        System.out.println("done add cleitn restriction");
    }
}
