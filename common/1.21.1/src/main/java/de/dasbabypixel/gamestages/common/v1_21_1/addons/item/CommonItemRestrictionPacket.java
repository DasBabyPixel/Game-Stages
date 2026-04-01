package de.dasbabypixel.gamestages.common.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenNetwork;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenNetwork.NetworkData;
import de.dasbabypixel.gamestages.common.v1_21_1.network.GameStagesPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

import static net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8;

public record CommonItemRestrictionPacket(@NonNull CommonItemCollection targetCollection, @NonNull String origin,
                                          @NonNull NetworkData<?> dataDrivenData) implements GameStagesPacket {
    public static final Type<CommonItemRestrictionPacket> TYPE = new Type<>(CommonVGameStageMod.location("item_restriction"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CommonItemRestrictionPacket> STREAM_CODEC = StreamCodec.ofMember(CommonItemRestrictionPacket::encode, CommonItemRestrictionPacket::new);

    public CommonItemRestrictionPacket(@NonNull RegistryFriendlyByteBuf byteBuf) {
        this(CommonItemCollection.STREAM_CODEC.decode(byteBuf), STRING_UTF8.decode(byteBuf), DataDrivenNetwork.DATA_CODEC.decode(byteBuf));
    }

    public void encode(@NonNull RegistryFriendlyByteBuf byteBuf) {
        CommonItemCollection.STREAM_CODEC.encode(byteBuf, targetCollection);
        STRING_UTF8.encode(byteBuf, origin);
        DataDrivenNetwork.DATA_CODEC.encode(byteBuf, dataDrivenData);
    }

    @Override
    public void handle() {
        VItemAddon.instance().handle(this);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
