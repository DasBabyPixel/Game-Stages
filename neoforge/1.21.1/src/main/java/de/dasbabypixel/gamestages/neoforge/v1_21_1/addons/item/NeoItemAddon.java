package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.CommonItemRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.VItemAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonJEI;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonKJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;

public class NeoItemAddon extends VItemAddon implements NeoAddon {
    @Override
    public void handle(CommonItemRestrictionPacket packet) {
        var entry = new NeoItemRestrictionEntry(packet.predicate(), RestrictionEntryOrigin.string(packet.origin()), packet.targetCollection());
        entry.setHideInJEI(packet.hideInJEI());
        entry.setHideTooltip(packet.hideTooltip());
        entry.setRenderItemName(packet.renderItemName());
        ClientGameStageManager.instance().addRestriction(entry);
    }

    @Override
    public NeoAddonKJS createKubeJSSupport() {
        return new ItemKJS();
    }

    @Override
    public NeoAddonJEI createJEISupport() {
        return new ItemJEI();
    }

    @Override
    public NeoAddonProbeJS createProbeJSSupport() {
        return new ItemProbeJS();
    }
}
