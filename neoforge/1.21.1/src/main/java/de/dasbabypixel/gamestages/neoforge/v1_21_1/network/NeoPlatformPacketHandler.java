package de.dasbabypixel.gamestages.neoforge.v1_21_1.network;

import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.types.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.v1_21_1.network.PlatformPacketHandler;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.CommonFluidRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.CommonItemRestrictionPacket;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.restriction.types.NeoFluidRestrictionEntry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.restriction.types.NeoItemRestrictionEntry;

public class NeoPlatformPacketHandler implements PlatformPacketHandler {
    @Override
    public void handle(CommonItemRestrictionPacket packet) {
        var entry = new NeoItemRestrictionEntry(packet.predicate(), RestrictionEntryOrigin.string(packet.origin()), packet.targetCollection());
        entry.setHideInJEI(packet.hideInJEI());
        entry.setHideTooltip(packet.hideTooltip());
        entry.setRenderItemName(packet.renderItemName());
        ClientGameStageManager.instance().addRestriction(entry);
    }

    @Override
    public void handle(CommonFluidRestrictionPacket packet) {
        var entry = new NeoFluidRestrictionEntry(packet.predicate(), RestrictionEntryOrigin.string(packet.origin()), packet.targetCollection());
        entry.setHideInJEI(packet.hideInJEI());
        ClientGameStageManager.instance().addRestriction(entry);
    }
}
