package de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.addon.ContentRegistry;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.PacketRegistry;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VContentRegistry;

public abstract class VRecipeAddon implements VAddon {
    private static VRecipeAddon instance;

    public VRecipeAddon() {
        instance = this;
    }

    public static VRecipeAddon instance() {
        return instance;
    }

    @Override
    public void registerCustomContent(ContentRegistry registry) {
        registry
                .prepare(CommonRecipeCollection.TYPE)
                .set(ContentRegistry.NAME, "recipe")
                .set(ContentRegistry.FLATTENER_FACTORY, new RecipeFlattenerFactory())
                .set(VContentRegistry.GAME_CONTENT_SERIALIZER, CommonRecipeCollection.SERIALIZER)
                .register();
    }

    @Override
    public void registerPackets(PacketRegistry registry) {
        registry.playClientBound(CommonRecipeRestrictionPacket.TYPE, CommonRecipeRestrictionPacket.STREAM_CODEC);
    }

    public abstract void handle(CommonRecipeRestrictionPacket packet);
}
