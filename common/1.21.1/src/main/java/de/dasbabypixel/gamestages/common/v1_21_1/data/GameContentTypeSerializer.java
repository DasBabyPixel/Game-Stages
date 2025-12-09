package de.dasbabypixel.gamestages.common.v1_21_1.data;

import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.NonNull;

public interface GameContentTypeSerializer<T extends TypedGameContent> {
    @NonNull ResourceKey<Registry<GameContentTypeSerializer<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(CommonVGameStageMod.location("game_content_type_serializer"));
    @NonNull StreamCodec<RegistryFriendlyByteBuf, GameContentType<?>> STREAM_CODEC = ByteBufCodecs
            .registry(REGISTRY_KEY)
            .dispatch(x -> ((CommonGameContentType<?>) x).serializer(), GameContentTypeSerializer::streamCodec);
    @NonNull GameContentTypeSerializer<CommonItemCollection> ITEM = () -> StreamCodec.unit(CommonItemCollection.TYPE);
    @NonNull GameContentTypeSerializer<CommonFluidCollection> FLUID = () -> StreamCodec.unit(CommonFluidCollection.TYPE);

    @NonNull StreamCodec<RegistryFriendlyByteBuf, GameContentType<T>> streamCodec();
}
