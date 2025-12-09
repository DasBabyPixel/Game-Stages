package de.dasbabypixel.gamestages.common.v1_21_1.data;

import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.ItemCollection;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;

import java.util.Collection;

public record CommonItemCollection(
        @NonNull HolderSet<@NonNull Item> items) implements ItemCollection, CommonGameContent {
    public static final CommonGameContentType<CommonItemCollection> TYPE = () -> GameContentTypeSerializer.ITEM;
    public static final CommonItemCollection EMPTY = new CommonItemCollection(HolderSet.empty());
    public static final StreamCodec<RegistryFriendlyByteBuf, CommonItemCollection> STREAM_CODEC = ByteBufCodecs
            .holderSet(Registries.ITEM)
            .map(CommonItemCollection::new, CommonItemCollection::items);

    @Override
    public @NonNull CommonGameContentSerializer<CommonItemCollection> serializer() {
        return CommonGameContentSerializer.ITEM_COLLECTION;
    }

    @Override
    public @NonNull GameContentType<?> type() {
        return TYPE;
    }

    @Override
    public @NonNull Collection<@NonNull Object> content() {
        return items.stream().map(s -> (Object) s).toList();
    }

    @Override
    public boolean isEmpty() {
        return items.size() == 0;
    }
}
