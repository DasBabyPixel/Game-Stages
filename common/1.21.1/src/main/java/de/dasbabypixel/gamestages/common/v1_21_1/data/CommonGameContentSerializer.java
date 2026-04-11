package de.dasbabypixel.gamestages.common.v1_21_1.data;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CommonGameContentSerializer<T extends CommonGameContent> {
    CommonGameContentSerializer<CommonGameContent.Except> EXCEPT = () -> CommonGameContent.Except.STREAM_CODEC;
    CommonGameContentSerializer<CommonGameContent.Only> ONLY = () -> CommonGameContent.Only.STREAM_CODEC;
    CommonGameContentSerializer<CommonGameContent.Union> UNION = () -> CommonGameContent.Union.STREAM_CODEC;
    CommonGameContentSerializer<CommonGameContent.FilterType> FILTER_TYPE = () -> CommonGameContent.FilterType.STREAM_CODEC;
    CommonGameContentSerializer<CommonGameContent.Mod> MOD = () -> CommonGameContent.Mod.STREAM_CODEC;

    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();
}
