package de.dasbabypixel.gamestages.common.v1_21_1.data;

import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.v1_21_1.data.f.CommonGameContentFlattener;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class ItemFlattenerFactory implements CommonGameContentFlattener.FlattenerFactory<CommonItemCollection> {
    @Override
    public @NonNull GameContentType<CommonItemCollection> type() {
        return CommonItemCollection.TYPE;
    }

    @Override
    public CommonGameContentFlattener.@NonNull Flattener<CommonItemCollection> createUnion() {
        return new CommonGameContentFlattener.Flattener<>() {
            private final List<HolderSet<Item>> holderSets = new ArrayList<>();

            @Override
            public void accept(@NonNull CommonItemCollection list) {
                holderSets.add(list.items());
            }

            @Override
            public @NonNull CommonItemCollection complete() {
                if (holderSets.isEmpty()) return CommonItemCollection.EMPTY;
                var holderSet = HolderSet.direct(holderSets.stream().flatMap(HolderSet::stream).toList());
                return new CommonItemCollection(holderSet);
            }
        };
    }

    @Override
    public CommonGameContentFlattener.@NonNull Flattener<CommonItemCollection> createOnly() {
        return new CommonGameContentFlattener.Flattener<>() {
            private Set<Holder<Item>> inclusions;
            private Stream<Holder<Item>> base;

            @Override
            public void accept(@NonNull CommonItemCollection list) {
                if (base == null) {
                    base = list.items().stream();
                    inclusions = new HashSet<>();
                } else inclusions.addAll(list.items().stream().toList());
            }

            @Override
            public @NonNull CommonItemCollection complete() {
                if (base == null) return CommonItemCollection.EMPTY;
                var holderSet = HolderSet.direct(base.filter(inclusions::contains).toList());
                return new CommonItemCollection(holderSet);
            }
        };
    }

    @Override
    public CommonGameContentFlattener.@NonNull Flattener<CommonItemCollection> createExcept() {
        return new CommonGameContentFlattener.Flattener<>() {
            private Set<Holder<Item>> exclusions;
            private Stream<Holder<Item>> base;

            @Override
            public void accept(@NonNull CommonItemCollection list) {
                if (base == null) {
                    base = list.items().stream();
                    exclusions = new HashSet<>();
                } else exclusions.addAll(list.items().stream().toList());
            }

            @Override
            public @NonNull CommonItemCollection complete() {
                if (base == null) return CommonItemCollection.EMPTY;
                var holderSet = HolderSet.direct(base.filter(h -> !exclusions.contains(h)).toList());
                return new CommonItemCollection(holderSet);
            }
        };
    }
}
