package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.common.v1_21_1.addons.fluid.FluidType;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.JSContext;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.JSParserBase;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.TypedGameCollectionJS;
import dev.latvian.mods.kubejs.fluid.FluidLike;
import dev.latvian.mods.rhino.Context;
import net.minecraft.core.registries.Registries;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class FluidJSParser extends JSParserBase {
    @SuppressWarnings("DataFlowIssue")
    public FluidJSParser() {
        registerRegistryHandlers(FluidLike.class, Registries.FLUID, FluidLike::kjs$getFluid, FluidType.get());
    }

    @Override
    public TypedGameCollectionJS parse(Context cx, Object @Nullable ... inputs) {
        var c = super.parse(cx, inputs);
        var type = JSContext.instance(cx).get(JSContext.Attributes.CONTENT_TYPES).type(FluidType.get());
        return c.filterType(type);
    }
}
