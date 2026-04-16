package de.dasbabypixel.gamestages.common.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.addons.item.ItemCollection;
import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonGameContent;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonGameContentSerializer;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonGameContentType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Objects;

@NullMarked
public record CommonItemCollection(HolderSet<Item> items) implements ItemCollection, CommonGameContent {
    public static final CommonItemCollection EMPTY = new CommonItemCollection(HolderSet.empty());
    @SuppressWarnings("DataFlowIssue")
    public static final StreamCodec<RegistryFriendlyByteBuf, CommonItemCollection> STREAM_CODEC = ByteBufCodecs.holderSet(Registries.ITEM)
            .map(CommonItemCollection::new, CommonItemCollection::items);
    public static final CommonGameContentSerializer<CommonItemCollection> SERIALIZER = () -> CommonItemCollection.STREAM_CODEC;
    public static final CommonGameContentType<CommonItemCollection> TYPE = new CommonGameContentType.AbstractGameContentType<>() {
        @Override
        public CommonItemCollection modContent(String modId) {
            var set = HolderSet.direct(BuiltInRegistries.ITEM.holders()
                    .filter(r -> modId.equals(Objects.requireNonNull(r).key().location().getNamespace()))
                    .filter(r -> !(r.value() instanceof AirItem))
                    .toList());
            return new CommonItemCollection(set);
        }
    };

    @Override
    public CommonGameContentSerializer<CommonItemCollection> serializer() {
        return SERIALIZER;
    }

    @Override
    public GameContentType<?> type() {
        return TYPE;
    }

    @Override
    public Iterable<Holder<Item>> content() {
        return items;
    }

    @Override
    public Collection<? extends Object> contentCollection() {
        return items.stream().toList();
    }

    @Override
    public boolean isEmpty() {
        return items.size() == 0;
    }
}
