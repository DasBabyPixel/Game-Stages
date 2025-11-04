package de.dasbabypixel.gamestages.common.v1_21_1.data;

import de.dasbabypixel.gamestages.common.data.ItemCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;

import java.util.stream.Stream;

public sealed interface CommonItemCollection<T extends CommonItemCollection<T>> extends ItemCollection<CommonItemCollection<?>> {
    @NonNull
    ResourceKey<Registry<CommonItemCollectionSerializer<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(CommonVGameStageMod.location("item_collection_serializer"));
    @NonNull
    StreamCodec<RegistryFriendlyByteBuf, CommonItemCollection<?>> STREAM_CODEC = ByteBufCodecs
            .registry(REGISTRY_KEY)
            .dispatch(CommonItemCollection::serializer, CommonItemCollectionSerializer::streamCodec);

    @Override
    default @NonNull CommonItemCollection<?> except(@NonNull ItemCollection<CommonItemCollection<?>> other) {
        return new Except(this, other.self());
    }

    @Override
    default @NonNull CommonItemCollection<?> only(@NonNull ItemCollection<CommonItemCollection<?>> other) {
        return other.except(this);
    }

    @Override
    default @NonNull CommonItemCollection<?> union(@NonNull ItemCollection<CommonItemCollection<?>> other) {
        return new Union(this, other.self());
    }

    @NonNull
    CommonItemCollectionSerializer<T> serializer();

    record Direct(@NonNull HolderSet<@NonNull Item> items) implements CommonItemCollection<Direct> {
        public static final StreamCodec<RegistryFriendlyByteBuf, Direct> STREAM_CODEC = ByteBufCodecs
                .holderSet(Registries.ITEM)
                .map(Direct::new, Direct::items);

        @Override
        public @NonNull CommonItemCollectionSerializer<Direct> serializer() {
            return CommonItemCollectionSerializer.DIRECT;
        }
    }

    record Except(@NonNull CommonItemCollection<?> base,
                  @NonNull CommonItemCollection<?> exclusion) implements CommonItemCollection<Except> {
        public static final StreamCodec<RegistryFriendlyByteBuf, Except> STREAM_CODEC = StreamCodec.composite(CommonItemCollection.STREAM_CODEC, Except::base, CommonItemCollection.STREAM_CODEC, Except::exclusion, Except::new);

        @Override
        public @NonNull CommonItemCollectionSerializer<Except> serializer() {
            return CommonItemCollectionSerializer.EXCEPT;
        }
    }

    record Union(@NonNull CommonItemCollection<?> c1,
                 @NonNull CommonItemCollection<?> c2) implements CommonItemCollection<Union> {
        public static final StreamCodec<RegistryFriendlyByteBuf, Union> STREAM_CODEC = StreamCodec.composite(CommonItemCollection.STREAM_CODEC, Union::c1, CommonItemCollection.STREAM_CODEC, Union::c2, Union::new);

        @Override
        public @NonNull CommonItemCollectionSerializer<Union> serializer() {
            return CommonItemCollectionSerializer.UNION;
        }
    }
}
