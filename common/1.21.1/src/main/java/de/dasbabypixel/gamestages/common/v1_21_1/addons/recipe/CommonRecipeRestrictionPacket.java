package de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import de.dasbabypixel.gamestages.common.v1_21_1.network.GameStagesPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

import static de.dasbabypixel.gamestages.common.v1_21_1.data.CommonCodecs.PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC;
import static net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8;

public record CommonRecipeRestrictionPacket(@NonNull PreparedRestrictionPredicate predicate,
                                            @NonNull CommonRecipeCollection targetCollection,
                                            @NonNull String origin) implements GameStagesPacket {
    public static final Type<CommonRecipeRestrictionPacket> TYPE = new Type<>(CommonVGameStageMod.location("recipe_restriction"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CommonRecipeRestrictionPacket> STREAM_CODEC = StreamCodec.ofMember(CommonRecipeRestrictionPacket::encode, CommonRecipeRestrictionPacket::new);

    public CommonRecipeRestrictionPacket(RegistryFriendlyByteBuf byteBuf) {
        this(PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC.decode(byteBuf), CommonRecipeCollection.STREAM_CODEC.decode(byteBuf), STRING_UTF8.decode(byteBuf));
    }

    public void encode(RegistryFriendlyByteBuf byteBuf) {
        PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC.encode(byteBuf, predicate);
        CommonRecipeCollection.STREAM_CODEC.encode(byteBuf, targetCollection);
        STRING_UTF8.encode(byteBuf, origin);
    }

    @Override
    public void handle() {
        VRecipeAddon.instance().handle(this);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
