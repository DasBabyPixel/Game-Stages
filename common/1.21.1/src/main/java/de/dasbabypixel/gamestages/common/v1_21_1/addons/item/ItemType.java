package de.dasbabypixel.gamestages.common.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.GameContentRegistry;
import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.v1_21_1.data.GameContentSerializers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@NullMarked
public class ItemType implements GameContentType<ItemType.ItemData, HolderSet<Item>, Holder<Item>> {
    private static GameContentRegistry.@Nullable Entry<?, ItemType.ItemData, HolderSet<Item>, Holder<Item>> TYPE;

    @SuppressWarnings("unchecked")
    public static GameContentRegistry.Entry<?, ItemData, HolderSet<Item>, Holder<Item>> get() {
        if (TYPE != null) return TYPE;
        return TYPE = (GameContentRegistry.Entry<?, @NonNull ItemData, @NonNull HolderSet<Item>, @NonNull Holder<Item>>) CommonInstances.gameContentRegistry.byTypeId("item");
    }

    public static void register(GameContentRegistry.Builder builder) {
        GameContentRegistry.Builder.Entry<? extends GameContentRegistry.Builder.Entry<?, ? extends GameContentRegistry.Entry<?, ItemData, HolderSet<Item>, Holder<Item>>, ItemData, HolderSet<Item>, Holder<Item>>, ? extends GameContentRegistry.Entry<?, ItemData, HolderSet<Item>, Holder<Item>>, ItemData, HolderSet<Item>, Holder<Item>> entry = builder.register("item", new ItemType());
        entry.init(GameContentSerializers.attributeStreamCodecElements(), ByteBufCodecs.holderSet(Registries.ITEM));
    }

    @Override
    public ItemData newTypeData(TypedGameContent<ItemData, HolderSet<Item>, Holder<Item>> content) {
        return new ItemData();
    }

    @Override
    public HolderSet<Item> modContent(String modId) {
        return HolderSet.direct(BuiltInRegistries.ITEM
                .holders()
                .filter(r -> modId.equals(Objects.requireNonNull(r).key().location().getNamespace()))
                .filter(r -> !(r.value() instanceof AirItem))
                .toList());
    }

    @Override
    public Iterable<Holder<Item>> iterate(HolderSet<Item> holders) {
        return holders;
    }

    @Override
    public ElementsBuilder<HolderSet<Item>, Holder<Item>> newElementsBuilder() {
        return new AbstractElementsBuilder<>() {
            @Override
            public HolderSet<Item> buildFromElementsList(List<HolderSet<Item>> holderSets) {
                return buildFromBoth(holderSets, Set.of());
            }

            @Override
            public HolderSet<Item> buildFromElementSet(Set<Holder<Item>> holders) {
                return HolderSet.direct(List.copyOf(holders));
            }

            @Override
            public HolderSet<Item> buildFromBoth(List<HolderSet<Item>> holderSets, Set<Holder<Item>> holders) {
                var h = new HashSet<>(holders);
                for (var holderSet : holderSets) {
                    for (var itemHolder : holderSet) {
                        h.add(Objects.requireNonNull(itemHolder));
                    }
                }
                return HolderSet.direct(List.copyOf(h));
            }
        };
    }

    public static class ItemData {
    }
}
