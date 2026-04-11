package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network;

import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.CommonItemCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.VItemAddon;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenNetwork.NetworkData;
import de.dasbabypixel.gamestages.common.v1_21_1.network.GameStagesPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NullMarked;

import static de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenNetwork.NETWORK_DATA_STREAM_CODEC;
import static net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8;

@NullMarked
public record CommonItemRestrictionPacket(CommonItemCollection targetCollection, String origin,
                                          NetworkData<?> dataDrivenData) implements GameStagesPacket {
    public static final Type<CommonItemRestrictionPacket> TYPE = new Type<>(CommonVGameStageMod.location("item_restriction"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CommonItemRestrictionPacket> STREAM_CODEC = StreamCodec.ofMember(CommonItemRestrictionPacket::encode, CommonItemRestrictionPacket::new);

    public CommonItemRestrictionPacket(RegistryFriendlyByteBuf byteBuf) {
        this(CommonItemCollection.STREAM_CODEC.decode(byteBuf), STRING_UTF8.decode(byteBuf), NETWORK_DATA_STREAM_CODEC.decode(byteBuf));
    }

    public void encode(RegistryFriendlyByteBuf byteBuf) {
        CommonItemCollection.STREAM_CODEC.encode(byteBuf, targetCollection);
        STRING_UTF8.encode(byteBuf, origin);
        NETWORK_DATA_STREAM_CODEC.encode(byteBuf, dataDrivenData);
    }

    @Override
    public void handle() {
        VItemAddon.instance().handle(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
