package de.dasbabypixel.gamestages.common.v1_21_1.data;

import de.dasbabypixel.gamestages.common.addons.fluid.FluidCollection;
import de.dasbabypixel.gamestages.common.data.GameContentType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Objects;

@NullMarked
public record CommonFluidCollection(HolderSet<Fluid> fluids) implements FluidCollection, CommonGameContent {
    public static final CommonGameContentType<CommonFluidCollection> TYPE = new CommonGameContentType.AbstractGameContentType<>() {
        @Override
        public CommonFluidCollection modContent(String modId) {
            var set = HolderSet.direct(BuiltInRegistries.FLUID.holders()
                    .filter(r -> modId.equals(Objects.requireNonNull(r).key().location().getNamespace()))
                    .toList());
            return new CommonFluidCollection(set);
        }
    };
    public static final CommonFluidCollection EMPTY = new CommonFluidCollection(HolderSet.empty());
    @SuppressWarnings("DataFlowIssue")
    public static final StreamCodec<RegistryFriendlyByteBuf, CommonFluidCollection> STREAM_CODEC = ByteBufCodecs.holderSet(Registries.FLUID)
            .map(CommonFluidCollection::new, CommonFluidCollection::fluids);
    public static final CommonGameContentSerializer<CommonFluidCollection> SERIALIZER = () -> CommonFluidCollection.STREAM_CODEC;

    @Override
    public GameContentType<?> type() {
        return TYPE;
    }

    @Override
    public Iterable<Holder<Fluid>> content() {
        return fluids;
    }

    @Override
    public Collection<? extends Object> contentCollection() {
        return fluids.stream().toList();
    }

    @Override
    public boolean isEmpty() {
        return fluids.size() == 0;
    }

    @Override
    public CommonGameContentSerializer<?> serializer() {
        return SERIALIZER;
    }
}
