package de.dasbabypixel.gamestages.common.v1_21_1;

import de.dasbabypixel.gamestages.common.BuildConstants;
import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.v1_21_1.network.PlatformPacketCreatorImpl;
import de.dasbabypixel.gamestages.common.v1_21_1.network.PlatformPacketHandler;
import net.minecraft.resources.ResourceLocation;

public class CommonVGameStageMod {
    public static PlatformPacketHandler platformPacketHandler;

    static {
        CommonInstances.platformPacketCreator = new PlatformPacketCreatorImpl();
    }

    public static void init() {
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(BuildConstants.MOD_ID, path);
    }
}
