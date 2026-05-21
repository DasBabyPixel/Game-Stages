package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;
import moe.wolfgirl.probejs.plugin.builtins.alias.RecordTypes;
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
    {
        RecordTypes.SKIP_RECORDS.add(FluidCollectionWrapper.class);
    }

    @Override
    public void addTypeAlias(AliasRegistrar registrar) {
        {
            var fluid = RegistryTypes.object("Fluid");
            var fluidExplicit = wrapped("`.${%s}`", fluid);
            var fluidTag = wrapped("`#${%s}`", RegistryTypes.tag("Fluid"));
            var mod = wrapped("`@${%s}`", SpecialTypes.MOD_ID);
            var recursive = Objects.requireNonNull(clazz(FluidCollectionWrapper.class).asInput()).asArray();
            var fluidWrapper = union(fluid, fluidExplicit, fluidTag, mod, recursive);
            registrar.addInputAlias(FluidCollectionWrapper.class, fluidWrapper.markAsInput());
        }
    }
}
