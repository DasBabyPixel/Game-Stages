package de.dasbabypixel.gamestages.common.v1_21_1.data;

import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.restriction.CompositePreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.predicates.And;
import de.dasbabypixel.gamestages.common.data.restriction.predicates.Or;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

public class CommonCodecs {
    public static final ResourceKey<Registry<RestrictionPredicateSerializer<?>>> RESTRICTION_PREDICATE_SERIALIZER_REGISTRY_KEY = ResourceKey.createRegistryKey(CommonVGameStageMod.location("restriction_predicate_serializer"));
    public static final ResourceKey<Registry<PreparedRestrictionPredicateSerializer<?>>> PREPARED_RESTRICTION_PREDICATE_SERIALIZER_REGISTRY_KEY = ResourceKey.createRegistryKey(CommonVGameStageMod.location("prepared_restriction_predicate_serializer"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RestrictionPredicate> RESTRICTION_PREDICATE_STREAM_CODEC = ByteBufCodecs
            .registry(RESTRICTION_PREDICATE_SERIALIZER_REGISTRY_KEY)
            .dispatch(CommonCodecs::serializer, RestrictionPredicateSerializer::streamCodec);
    public static final StreamCodec<RegistryFriendlyByteBuf, PreparedRestrictionPredicate> PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC = ByteBufCodecs
            .registry(PREPARED_RESTRICTION_PREDICATE_SERIALIZER_REGISTRY_KEY)
            .dispatch(CommonCodecs::serializer, PreparedRestrictionPredicateSerializer::streamCodec);

    public static RestrictionPredicateSerializer<?> serializer(RestrictionPredicate predicate) {
        return switch (predicate) {
            case GameStage ignored -> RestrictionPredicateSerializer.GAME_STAGE;
            case And ignored -> RestrictionPredicateSerializer.AND;
            case Or ignored -> RestrictionPredicateSerializer.OR;
            default -> throw new IllegalStateException("Unexpected value: " + predicate);
        };
    }

    private static PreparedRestrictionPredicateSerializer<?> serializer(PreparedRestrictionPredicate predicate) {
        return switch (predicate) {
            case GameStage ignored -> PreparedRestrictionPredicateSerializer.GAME_STAGE;
            case CompositePreparedRestrictionPredicate ignored -> PreparedRestrictionPredicateSerializer.COMPOSITE;
            default -> throw new IllegalStateException("Unexpected value: " + predicate);
        };
    }

    public interface PreparedRestrictionPredicateSerializer<T extends PreparedRestrictionPredicate> {
        StreamCodec<ByteBuf, GameStage> GAME_STAGE_STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(GameStage::new, GameStage::name);
        StreamCodec<RegistryFriendlyByteBuf, CompositePreparedRestrictionPredicate> COMPOSITE_STREAM_CODEC = StreamCodec.composite(RESTRICTION_PREDICATE_STREAM_CODEC, CompositePreparedRestrictionPredicate::predicate, PREPARED_RESTRICTION_PREDICATE_STREAM_CODEC.apply(ByteBufCodecs.list()), CompositePreparedRestrictionPredicate::dependencies, CompositePreparedRestrictionPredicate::new);
        PreparedRestrictionPredicateSerializer<GameStage> GAME_STAGE = () -> GAME_STAGE_STREAM_CODEC;
        PreparedRestrictionPredicateSerializer<CompositePreparedRestrictionPredicate> COMPOSITE = () -> COMPOSITE_STREAM_CODEC;

        StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();
    }

    public interface RestrictionPredicateSerializer<T extends RestrictionPredicate> {
        StreamCodec<ByteBuf, GameStage> GAME_STAGE_STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(GameStage::new, GameStage::name);
        StreamCodec<ByteBuf, And> AND_STREAM_CODEC = StreamCodec.unit(And.INSTANCE);
        StreamCodec<ByteBuf, Or> OR_STREAM_CODEC = StreamCodec.unit(Or.INSTANCE);
        RestrictionPredicateSerializer<GameStage> GAME_STAGE = () -> GAME_STAGE_STREAM_CODEC;
        RestrictionPredicateSerializer<And> AND = () -> AND_STREAM_CODEC;
        RestrictionPredicateSerializer<Or> OR = () -> OR_STREAM_CODEC;

        StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();
    }
}
