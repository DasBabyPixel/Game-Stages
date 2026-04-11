package de.dasbabypixel.gamestages.common.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener.Flattener;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener.FlattenerFactory;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

@NullMarked
public class ItemFlattenerFactory implements FlattenerFactory<CommonItemCollection> {
    @Override
    public GameContentType<CommonItemCollection> type() {
        return CommonItemCollection.TYPE;
    }

    @Override
    public Flattener<CommonItemCollection> createUnion() {
        return new Flattener<>() {
            private final List<HolderSet<Item>> holderSets = new ArrayList<>();

            @Override
            public void accept(CommonItemCollection list) {
                holderSets.add(list.items());
            }

            @Override
            public CommonItemCollection complete() {
                if (holderSets.isEmpty()) return CommonItemCollection.EMPTY;
                var holderSet = HolderSet.direct(holderSets.stream().flatMap(HolderSet::stream).toList());
                return new CommonItemCollection(holderSet);
            }
        };
    }

    @Override
    public Flattener<CommonItemCollection> createOnly() {
        return new Flattener<>() {
            private @Nullable Set<Holder<Item>> inclusions;
            private @Nullable Stream<Holder<Item>> base;

            @Override
            public void accept(CommonItemCollection list) {
                if (base == null) {
                    base = list.items().stream();
                    inclusions = new HashSet<>();
                } else Objects.requireNonNull(inclusions).addAll(list.items().stream().toList());
            }

            @Override
            public CommonItemCollection complete() {
                if (base == null) return CommonItemCollection.EMPTY;
                Objects.requireNonNull(inclusions);
                var holderSet = HolderSet.direct(base.filter(inclusions::contains).toList());
                return new CommonItemCollection(holderSet);
            }
        };
    }

    @Override
    public Flattener<CommonItemCollection> createExcept() {
        return new Flattener<>() {
            private @Nullable Set<Holder<Item>> exclusions;
            private @Nullable Stream<Holder<Item>> base;

            @Override
            public void accept(CommonItemCollection list) {
                if (base == null) {
                    base = list.items().stream();
                    exclusions = new HashSet<>();
                } else Objects.requireNonNull(exclusions).addAll(list.items().stream().toList());
            }

            @Override
            public CommonItemCollection complete() {
                if (base == null) return CommonItemCollection.EMPTY;
                Objects.requireNonNull(exclusions);
                var holderSet = HolderSet.direct(base.filter(h -> !exclusions.contains(h)).toList());
                return new CommonItemCollection(holderSet);
            }
        };
    }
}
