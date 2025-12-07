package de.dasbabypixel.gamestages.common.v1_21_1.data;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

public interface CommonGameContentSerializer<T extends CommonGameContent> {
    @NonNull CommonGameContentSerializer<CommonItemCollection> ITEM_COLLECTION = () -> CommonItemCollection.STREAM_CODEC;
    @NonNull CommonGameContentSerializer<CommonGameContent.Except> EXCEPT = () -> CommonGameContent.Except.STREAM_CODEC;
    @NonNull CommonGameContentSerializer<CommonGameContent.Only> ONLY = () -> CommonGameContent.Only.STREAM_CODEC;
    @NonNull CommonGameContentSerializer<CommonGameContent.Union> UNION = () -> CommonGameContent.Union.STREAM_CODEC;
    @NonNull CommonGameContentSerializer<CommonGameContent.FilterType> FILTER_TYPE = () -> CommonGameContent.FilterType.STREAM_CODEC;

    @NonNull StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();
}
