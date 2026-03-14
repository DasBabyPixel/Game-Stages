package de.dasbabypixel.gamestages.common.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.addon.ContentRegistry;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.PacketRegistry;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VContentRegistry;

public abstract class VItemAddon implements VAddon {
    private static VItemAddon instance;

    public VItemAddon() {
        instance = this;
    }

    public static VItemAddon instance() {
        return instance;
    }

    @Override
    public void registerCustomContent(ContentRegistry registry) {
        registry
                .prepare(CommonItemCollection.TYPE)
                .set(ContentRegistry.NAME, "item")
                .set(ContentRegistry.FLATTENER_FACTORY, new ItemFlattenerFactory())
                .set(VContentRegistry.GAME_CONTENT_SERIALIZER, CommonItemCollection.SERIALIZER)
                .register();
    }

    @Override
    public void registerPackets(PacketRegistry registry) {
        registry.playClientBound(CommonItemRestrictionPacket.TYPE, CommonItemRestrictionPacket.STREAM_CODEC);
    }

    public abstract void handle(CommonItemRestrictionPacket packet);
}
