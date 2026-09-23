package de.dasbabypixel.gamestages.common.v1_21_1;

import de.dasbabypixel.gamestages.common.BuildConstants;
import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.GameContentDirect;
import de.dasbabypixel.gamestages.common.data.GameContentRegistry;
import de.dasbabypixel.gamestages.common.data.GameContentWrapper;
import de.dasbabypixel.gamestages.common.v1_21_1.data.GameContentSerializers;
import de.dasbabypixel.gamestages.common.v1_21_1.network.PlatformPacketCreatorImpl;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NullMarked;

import java.util.function.Function;

@SuppressWarnings("NotNullFieldNotInitialized")
@NullMarked
public class CommonVGameStageMod {
    public static GameContentSerializers gameContentSerializers;

    static {
        CommonInstances.platformPacketCreator = new PlatformPacketCreatorImpl();
    }

    public static void init() {
    }

    public static <Wrapper extends GameContentWrapper, TypeData, Elements, Element> StreamCodec<? super RegistryFriendlyByteBuf, Wrapper> directStreamCodec(GameContentRegistry.Entry<?, TypeData, Elements, Element> typeEntry, Function<? super GameContentDirect<TypeData, Elements, Element>, ? extends Wrapper> wrap, Function<? super Wrapper, ? extends GameContentDirect<TypeData, Elements, Element>> unwrap) {
        return CommonVGameStageMod.gameContentSerializers.directStreamCodec(typeEntry).map(wrap, unwrap);
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(BuildConstants.MOD_ID, path);
    }
}
