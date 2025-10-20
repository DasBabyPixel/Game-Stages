package de.dasbabypixel.gamestages.common.v1_21_1;

import de.dasbabypixel.gamestages.common.BuildConstants;
import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.v1_21_1.network.PlatformPacketCreatorImpl;
import net.minecraft.resources.ResourceLocation;

public class CommonVGameStageMod {
    static {
        CommonInstances.platformPacketCreator = new PlatformPacketCreatorImpl();
    }

    public static void init() {
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(BuildConstants.MOD_ID, path);
    }
}
