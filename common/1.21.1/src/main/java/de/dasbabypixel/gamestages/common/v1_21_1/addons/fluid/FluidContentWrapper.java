package de.dasbabypixel.gamestages.common.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.common.data.GameContentDirect;
import de.dasbabypixel.gamestages.common.data.GameContentWrapper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record FluidContentWrapper(
        GameContentDirect<FluidType.FluidData, HolderSet<Fluid>, Holder<Fluid>> gameContent) implements GameContentWrapper.Direct {
}
