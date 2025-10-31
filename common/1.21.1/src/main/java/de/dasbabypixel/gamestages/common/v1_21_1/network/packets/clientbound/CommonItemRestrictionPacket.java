package de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound;

import de.dasbabypixel.gamestages.common.client.network.ClientNetworkHandlers;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonItemCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.network.GameStagesPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

import static de.dasbabypixel.gamestages.common.v1_21_1.data.CommonCodecs.PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC;
import static net.minecraft.network.codec.ByteBufCodecs.BOOL;

public record CommonItemRestrictionPacket(@NonNull PreparedRestrictionPredicate predicate,
                                          @NonNull CommonItemCollection<?> targetCollection, boolean hideTooltip,
                                          boolean renderItemName, boolean hideInJEI) implements GameStagesPacket {
    public static final Type<CommonItemRestrictionPacket> TYPE = new Type<>(CommonVGameStageMod.location("item_restriction"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CommonItemRestrictionPacket> STREAM_CODEC = StreamCodec.ofMember(CommonItemRestrictionPacket::encode, CommonItemRestrictionPacket::new);

    public CommonItemRestrictionPacket(RegistryFriendlyByteBuf byteBuf) {
        this(PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC.decode(byteBuf), CommonItemCollection.STREAM_CODEC.decode(byteBuf), BOOL.decode(byteBuf), BOOL.decode(byteBuf), BOOL.decode(byteBuf));
    }

    public void encode(RegistryFriendlyByteBuf byteBuf) {
        PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC.encode(byteBuf, predicate);
        CommonItemCollection.STREAM_CODEC.encode(byteBuf, targetCollection);
        BOOL.encode(byteBuf, hideTooltip);
        BOOL.encode(byteBuf, renderItemName);
        BOOL.encode(byteBuf, hideInJEI);
    }

    @Override
    public void handle() {
        ClientNetworkHandlers.itemRestriction(predicate, targetCollection, hideTooltip, renderItemName, hideInJEI);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
