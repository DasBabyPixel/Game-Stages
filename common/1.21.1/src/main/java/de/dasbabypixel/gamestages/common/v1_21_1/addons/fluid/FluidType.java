package de.dasbabypixel.gamestages.common.v1_21_1.addons.fluid;

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
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@NullMarked
public class FluidType implements GameContentType<FluidType.FluidData, HolderSet<Fluid>, Holder<Fluid>> {
    private static GameContentRegistry.@Nullable Entry<?, FluidData, HolderSet<Fluid>, Holder<Fluid>> TYPE;

    @SuppressWarnings("unchecked")
    public static GameContentRegistry.Entry<?, FluidType.FluidData, HolderSet<Fluid>, Holder<Fluid>> get() {
        if (TYPE != null) return TYPE;
        return TYPE = (GameContentRegistry.Entry<?, @NonNull FluidData, @NonNull HolderSet<Fluid>, @NonNull Holder<Fluid>>) CommonInstances.gameContentRegistry.byTypeId("fluid");
    }

    public static void register(GameContentRegistry.Builder builder) {
        GameContentRegistry.Builder.Entry<? extends GameContentRegistry.Builder.Entry<?, ? extends GameContentRegistry.Entry<?, FluidData, HolderSet<Fluid>, Holder<Fluid>>, FluidData, HolderSet<Fluid>, Holder<Fluid>>, ? extends GameContentRegistry.Entry<?, FluidData, HolderSet<Fluid>, Holder<Fluid>>, FluidData, HolderSet<Fluid>, Holder<Fluid>> entry = builder.register("fluid", new FluidType());
        entry.init(GameContentSerializers.attributeStreamCodecElements(), ByteBufCodecs.holderSet(Registries.FLUID));
    }

    @Override
    public FluidData newTypeData(TypedGameContent<FluidData, HolderSet<Fluid>, Holder<Fluid>> content) {
        return new FluidData();
    }

    @Override
    public String toStringElements(HolderSet<Fluid> holders) {
        return switch (holders) {
            case HolderSet.Named<Fluid> named -> "#" + named.key().location();
            case HolderSet.Direct<Fluid> direct -> GameContentType.super.toStringElements(direct);
            default -> Objects.requireNonNull(holders.toString());
        };
    }

    @Override
    public String toStringElement(Holder<Fluid> itemHolder) {
        return itemHolder.getRegisteredName();
    }


    @Override
    public HolderSet<Fluid> modContent(String modId) {
        return HolderSet.direct(BuiltInRegistries.FLUID
                .holders()
                .filter(r -> modId.equals(Objects.requireNonNull(r).key().location().getNamespace()))
                .toList());
    }

    @Override
    public Iterable<Holder<Fluid>> iterate(HolderSet<Fluid> holders) {
        return holders;
    }

    @Override
    public ElementsBuilder<HolderSet<Fluid>, Holder<Fluid>> newElementsBuilder() {
        return new AbstractElementsBuilder<>() {
            @Override
            public HolderSet<Fluid> buildFromElementsList(List<HolderSet<Fluid>> holderSets) {
                return buildFromBoth(holderSets, Set.of());
            }

            @Override
            public HolderSet<Fluid> buildFromElementSet(Set<Holder<Fluid>> holders) {
                return HolderSet.direct(List.copyOf(holders));
            }

            @Override
            public HolderSet<Fluid> buildFromBoth(List<HolderSet<Fluid>> holderSets, Set<Holder<Fluid>> holders) {
                var h = new ArrayList<>(holders);
                for (var holderSet : holderSets) {
                    for (var itemHolder : holderSet) {
                        h.add(Objects.requireNonNull(itemHolder));
                    }
                }
                return HolderSet.direct(h);
            }
        };
    }

    public static class FluidData {
    }
}
