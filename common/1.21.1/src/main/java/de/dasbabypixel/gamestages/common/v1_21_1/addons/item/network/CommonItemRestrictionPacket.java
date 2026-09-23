package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network;

import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.ItemContentWrapper;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.ItemType;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.VItemAddon;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenNetwork.NetworkData;
import de.dasbabypixel.gamestages.common.v1_21_1.network.GameStagesPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NullMarked;

import static net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8;

@NullMarked
public record CommonItemRestrictionPacket(ItemContentWrapper targetCollection, String origin,
                                          NetworkData<?> dataDrivenData) implements GameStagesPacket {
    public static final Type<CommonItemRestrictionPacket> TYPE = new Type<>(CommonVGameStageMod.location("item_restriction"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CommonItemRestrictionPacket> STREAM_CODEC = StreamCodec.ofMember(CommonItemRestrictionPacket::encode, CommonItemRestrictionPacket::new);
    private static final StreamCodec<? super RegistryFriendlyByteBuf, ItemContentWrapper> STREAM_CODEC_DIRECT = CommonVGameStageMod.directStreamCodec(ItemType.get(), ItemContentWrapper::new, ItemContentWrapper::gameContent);

    public CommonItemRestrictionPacket(RegistryFriendlyByteBuf byteBuf) {
        this(STREAM_CODEC_DIRECT.decode(byteBuf), STRING_UTF8.decode(byteBuf), NetworkData.STREAM_CODEC.decode(byteBuf));
    }

    public void encode(RegistryFriendlyByteBuf byteBuf) {
        STREAM_CODEC_DIRECT.encode(byteBuf, targetCollection);
        STRING_UTF8.encode(byteBuf, origin);
        NetworkData.STREAM_CODEC.encode(byteBuf, dataDrivenData);
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
