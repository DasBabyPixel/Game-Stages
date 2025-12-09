package de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound;

import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonFluidCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.network.GameStagesPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

import static de.dasbabypixel.gamestages.common.v1_21_1.data.CommonCodecs.PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC;
import static net.minecraft.network.codec.ByteBufCodecs.BOOL;
import static net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8;

public record CommonFluidRestrictionPacket(@NonNull PreparedRestrictionPredicate predicate,
                                           @NonNull CommonFluidCollection targetCollection, @NonNull String origin,
                                           boolean hideInJEI) implements GameStagesPacket {
    public static final Type<CommonFluidRestrictionPacket> TYPE = new Type<>(CommonVGameStageMod.location("fluid_restriction"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CommonFluidRestrictionPacket> STREAM_CODEC = StreamCodec.ofMember(CommonFluidRestrictionPacket::encode, CommonFluidRestrictionPacket::new);

    public CommonFluidRestrictionPacket(RegistryFriendlyByteBuf buf) {
        this(PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC.decode(buf), CommonFluidCollection.STREAM_CODEC.decode(buf), STRING_UTF8.decode(buf), BOOL.decode(buf));
    }

    @Override
    public void handle() {
        CommonVGameStageMod.platformPacketHandler.handle(this);
    }

    private void encode(RegistryFriendlyByteBuf byteBuf) {
        PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC.encode(byteBuf, predicate);
        CommonFluidCollection.STREAM_CODEC.encode(byteBuf, targetCollection);
        STRING_UTF8.encode(byteBuf, origin);
        BOOL.encode(byteBuf, hideInJEI);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
