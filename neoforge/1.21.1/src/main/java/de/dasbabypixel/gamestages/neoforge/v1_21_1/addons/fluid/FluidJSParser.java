package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonFluidCollection;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.JSParserBase;
import dev.latvian.mods.kubejs.fluid.FluidLike;
import net.minecraft.core.registries.BuiltInRegistries;

public class FluidJSParser extends JSParserBase {
    public FluidJSParser() {
        registerRegistryHandlers(FluidLike.class, BuiltInRegistries.FLUID, FluidLike::kjs$getFluid, CommonFluidCollection::new, CommonFluidCollection.TYPE);
    }
}
