package de.dasbabypixel.gamestages.common.v1_21_1.network.util;

import com.mojang.serialization.Codec;
import de.dasbabypixel.gamestages.common.data.GameStage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public final class GameStagePayload {
    public static final StreamCodec<FriendlyByteBuf, GameStage> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, GameStage::name, GameStage::new);
    public static final StreamCodec<FriendlyByteBuf, List<GameStage>> STREAM_CODEC_LIST = STREAM_CODEC.apply(ByteBufCodecs.list());
    public static final Codec<GameStage> CODEC = Codec.STRING.xmap(GameStage::new, GameStage::name);
    public static final Codec<List<GameStage>> CODEC_LIST = CODEC.listOf();
}
