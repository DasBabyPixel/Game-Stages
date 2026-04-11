package de.dasbabypixel.gamestages.common.v1_21_1.data.flattener;

import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener.Flattener;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener.FlattenerFactory;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonFluidCollection;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

@NullMarked
public class FluidFlattenerFactory implements FlattenerFactory<CommonFluidCollection> {
    @Override
    public GameContentType<CommonFluidCollection> type() {
        return CommonFluidCollection.TYPE;
    }

    @Override
    public Flattener<CommonFluidCollection> createUnion() {
        return new Flattener<>() {
            private final List<HolderSet<Fluid>> holderSets = new ArrayList<>();

            @Override
            public void accept(CommonFluidCollection list) {
                holderSets.add(list.fluids());
            }

            @Override
            public CommonFluidCollection complete() {
                if (holderSets.isEmpty()) return CommonFluidCollection.EMPTY;
                var holderSet = HolderSet.direct(holderSets.stream().flatMap(HolderSet::stream).toList());
                return new CommonFluidCollection(holderSet);
            }
        };
    }

    @Override
    public Flattener<CommonFluidCollection> createOnly() {
        return new Flattener<>() {
            private @Nullable Set<Holder<Fluid>> inclusions;
            private @Nullable Stream<Holder<Fluid>> base;

            @Override
            public void accept(CommonFluidCollection list) {
                if (base == null) {
                    base = list.fluids().stream();
                    inclusions = new HashSet<>();
                } else Objects.requireNonNull(inclusions).addAll(list.fluids().stream().toList());
            }

            @Override
            public CommonFluidCollection complete() {
                if (base == null) return CommonFluidCollection.EMPTY;
                Objects.requireNonNull(inclusions);
                var holderSet = HolderSet.direct(base.filter(inclusions::contains).toList());
                return new CommonFluidCollection(holderSet);
            }
        };
    }

    @Override
    public Flattener<CommonFluidCollection> createExcept() {
        return new Flattener<>() {
            private @Nullable Set<Holder<Fluid>> exclusions;
            private @Nullable Stream<Holder<Fluid>> base;

            @Override
            public void accept(CommonFluidCollection list) {
                if (base == null) {
                    base = list.fluids().stream();
                    exclusions = new HashSet<>();
                } else Objects.requireNonNull(exclusions).addAll(list.fluids().stream().toList());
            }

            @Override
            public CommonFluidCollection complete() {
                if (base == null) return CommonFluidCollection.EMPTY;
                Objects.requireNonNull(exclusions);
                var holderSet = HolderSet.direct(base.filter(not(exclusions::contains)).toList());
                return new CommonFluidCollection(holderSet);
            }
        };
    }
}
