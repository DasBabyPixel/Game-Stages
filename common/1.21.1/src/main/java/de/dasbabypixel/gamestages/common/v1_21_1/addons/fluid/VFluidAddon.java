package de.dasbabypixel.gamestages.common.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.common.addon.ContentRegistry;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.PacketRegistry;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VContentRegistry;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonFluidCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.data.flattener.FluidFlattenerFactory;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.CommonFluidRestrictionPacket;

public abstract class VFluidAddon implements VAddon {
    private static VFluidAddon instance;

    public VFluidAddon() {
        instance = this;
    }

    public static VFluidAddon instance() {
        return instance;
    }

    @Override
    public void registerCustomContent(ContentRegistry registry) {
        registry
                .prepare(CommonFluidCollection.TYPE)
                .set(ContentRegistry.NAME, "fluid")
                .set(ContentRegistry.FLATTENER_FACTORY, new FluidFlattenerFactory())
                .set(VContentRegistry.GAME_CONTENT_SERIALIZER, CommonFluidCollection.SERIALIZER)
                .register();
    }

    @Override
    public void registerPackets(PacketRegistry registry) {
        registry.playClientBound(CommonFluidRestrictionPacket.TYPE, CommonFluidRestrictionPacket.STREAM_CODEC);
    }

    public abstract void handle(CommonFluidRestrictionPacket packet);
}
