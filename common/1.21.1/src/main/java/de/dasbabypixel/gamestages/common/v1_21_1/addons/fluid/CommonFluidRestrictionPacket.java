package de.dasbabypixel.gamestages.common.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import de.dasbabypixel.gamestages.common.v1_21_1.network.GameStagesPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NullMarked;

import static de.dasbabypixel.gamestages.common.v1_21_1.data.CommonCodecs.PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC;
import static net.minecraft.network.codec.ByteBufCodecs.BOOL;
import static net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8;

@NullMarked
public record CommonFluidRestrictionPacket(PreparedRestrictionPredicate predicate, FluidContentWrapper targetCollection,
                                           String origin, boolean hideInJEI) implements GameStagesPacket {
    public static final Type<CommonFluidRestrictionPacket> TYPE = new Type<>(CommonVGameStageMod.location("fluid_restriction"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CommonFluidRestrictionPacket> STREAM_CODEC = StreamCodec.ofMember(CommonFluidRestrictionPacket::encode, CommonFluidRestrictionPacket::new);
    private static final StreamCodec<? super RegistryFriendlyByteBuf, FluidContentWrapper> STREAM_CODEC_DIRECT = CommonVGameStageMod.directStreamCodec(FluidType.get(), FluidContentWrapper::new, FluidContentWrapper::gameContent);

    public CommonFluidRestrictionPacket(RegistryFriendlyByteBuf buf) {
        this(PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC.decode(buf), STREAM_CODEC_DIRECT.decode(buf), STRING_UTF8.decode(buf), BOOL.decode(buf));
    }

    @Override
    public void handle() {
        VFluidAddon.instance().handle(this);
    }

    private void encode(RegistryFriendlyByteBuf byteBuf) {
        PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC.encode(byteBuf, predicate);
        STREAM_CODEC_DIRECT.encode(byteBuf, targetCollection);
        STRING_UTF8.encode(byteBuf, origin);
        BOOL.encode(byteBuf, hideInJEI);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
