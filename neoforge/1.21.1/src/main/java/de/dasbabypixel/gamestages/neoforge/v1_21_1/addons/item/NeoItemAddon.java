package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactories;
import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.CommonItemRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.VItemAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonJEI;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonKJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;
import org.jspecify.annotations.NonNull;

public class NeoItemAddon extends VItemAddon implements NeoAddon {
    @Override
    public void handle(@NonNull CommonItemRestrictionPacket packet) {
        var factory = ItemStackRestrictionResolverFactories.instance().getFactory(packet.dataDrivenData().factoryId());
        if (factory == null) throw new IllegalStateException("Unknown factory: " + packet.dataDrivenData().factoryId());
        var networkData = packet.dataDrivenData();
        var entry = new NeoItemRestrictionEntry(RestrictionEntryOrigin.string(packet.origin()), packet.targetCollection(), networkData);
        ClientGameStageManager.instance().addRestriction(entry);
    }

    @Override
    public @NonNull NeoAddonKJS createKubeJSSupport() {
        return new ItemKJS();
    }

    @Override
    public @NonNull NeoAddonJEI createJEISupport() {
        return new ItemJEI();
    }

    @Override
    public @NonNull NeoAddonProbeJS createProbeJSSupport() {
        return new ItemProbeJS();
    }
}
