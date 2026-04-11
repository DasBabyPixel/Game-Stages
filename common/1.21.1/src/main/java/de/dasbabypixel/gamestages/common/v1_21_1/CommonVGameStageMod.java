package de.dasbabypixel.gamestages.common.v1_21_1;

import de.dasbabypixel.gamestages.common.BuildConstants;
import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.attribute.Attribute;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.v1_21_1.data.flattener.CommonGameContentFlattener;
import de.dasbabypixel.gamestages.common.v1_21_1.network.PlatformPacketCreatorImpl;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CommonVGameStageMod {
    static {
        CommonInstances.platformPacketCreator = new PlatformPacketCreatorImpl();
        GameContentFlattener.Attribute.INSTANCE = new Attribute<>(CommonGameContentFlattener::new);
    }

    public static void init() {
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(BuildConstants.MOD_ID, path);
    }
}
