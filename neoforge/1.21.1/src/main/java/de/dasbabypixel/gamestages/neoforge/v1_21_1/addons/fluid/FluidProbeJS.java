package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.common.v1_21_1.addons.fluid.FluidType;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.probejs.StagesProbeJSPlugin;
import moe.wolfgirl.probejs.plugin.builtins.alias.RegistryTypes;
import moe.wolfgirl.probejs.plugin.builtins.alias.SpecialTypes;
import moe.wolfgirl.probejs.typescript.base.AliasRegistrar;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

import static moe.wolfgirl.probejs.typescript.document.Types.clazz;
import static moe.wolfgirl.probejs.typescript.document.Types.union;
import static moe.wolfgirl.probejs.typescript.document.Types.wrapped;

@NullMarked
public class FluidProbeJS implements NeoAddonProbeJS {
    @Override
    public void addTypeAlias(AliasRegistrar registrar) {
        var self = StagesProbeJSPlugin.typedCompletionsClassPath(FluidType.get());
        var fluid = RegistryTypes.object("Fluid");
        var fluidExplicit = wrapped("`.${%s}`", fluid);
        var fluidTag = wrapped("`#${%s}`", RegistryTypes.tag("Fluid"));
        var mod = wrapped("`@${%s}`", SpecialTypes.MOD_ID);
        var recursive = Objects.requireNonNull(clazz(self).asInput()).asArray();
        var fluidWrapper = union(fluid, fluidExplicit, fluidTag, mod, recursive);
        registrar.addInputAlias(self, fluidWrapper.markAsInput());
    }
}
