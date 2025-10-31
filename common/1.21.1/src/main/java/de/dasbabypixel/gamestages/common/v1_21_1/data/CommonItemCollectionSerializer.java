package de.dasbabypixel.gamestages.common.v1_21_1.data;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

public interface CommonItemCollectionSerializer<T extends CommonItemCollection<T>> {
    @NonNull CommonItemCollectionSerializer<CommonItemCollection.Direct> DIRECT = () -> CommonItemCollection.Direct.STREAM_CODEC;
    @NonNull CommonItemCollectionSerializer<CommonItemCollection.Except> EXCEPT = () -> CommonItemCollection.Except.STREAM_CODEC;
    @NonNull CommonItemCollectionSerializer<CommonItemCollection.Union> UNION = () -> CommonItemCollection.Union.STREAM_CODEC;

    @NonNull StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();
}
