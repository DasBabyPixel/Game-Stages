package de.dasbabypixel.gamestages.common.v1_21_1.data.flattener;

import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener.Flattener;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener.FlattenerFactory;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonFluidCollection;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class FluidFlattenerFactory implements FlattenerFactory<CommonFluidCollection> {
    @Override
    public @NonNull GameContentType<CommonFluidCollection> type() {
        return CommonFluidCollection.TYPE;
    }

    @Override
    public @NonNull Flattener<CommonFluidCollection> createUnion() {
        return new Flattener<>() {
            private final List<HolderSet<Fluid>> holderSets = new ArrayList<>();

            @Override
            public void accept(@NonNull CommonFluidCollection list) {
                holderSets.add(list.fluids());
            }

            @Override
            public @NonNull CommonFluidCollection complete() {
                if (holderSets.isEmpty()) return CommonFluidCollection.EMPTY;
                var holderSet = HolderSet.direct(holderSets.stream().flatMap(HolderSet::stream).toList());
                return new CommonFluidCollection(holderSet);
            }
        };
    }

    @Override
    public @NonNull Flattener<CommonFluidCollection> createOnly() {
        return new Flattener<>() {
            private Set<Holder<Fluid>> inclusions;
            private Stream<Holder<Fluid>> base;

            @Override
            public void accept(@NonNull CommonFluidCollection list) {
                if (base == null) {
                    base = list.fluids().stream();
                    inclusions = new HashSet<>();
                } else inclusions.addAll(list.fluids().stream().toList());
            }

            @Override
            public @NonNull CommonFluidCollection complete() {
                if (base == null) return CommonFluidCollection.EMPTY;
                var holderSet = HolderSet.direct(base.filter(inclusions::contains).toList());
                return new CommonFluidCollection(holderSet);
            }
        };
    }

    @Override
    public @NonNull Flattener<CommonFluidCollection> createExcept() {
        return new Flattener<>() {
            private Set<Holder<Fluid>> exclusions;
            private Stream<Holder<Fluid>> base;

            @Override
            public void accept(@NonNull CommonFluidCollection list) {
                if (base == null) {
                    base = list.fluids().stream();
                    exclusions = new HashSet<>();
                } else exclusions.addAll(list.fluids().stream().toList());
            }

            @Override
            public @NonNull CommonFluidCollection complete() {
                if (base == null) return CommonFluidCollection.EMPTY;
                var holderSet = HolderSet.direct(base.filter(h -> !exclusions.contains(h)).toList());
                return new CommonFluidCollection(holderSet);
            }
        };
    }
}
