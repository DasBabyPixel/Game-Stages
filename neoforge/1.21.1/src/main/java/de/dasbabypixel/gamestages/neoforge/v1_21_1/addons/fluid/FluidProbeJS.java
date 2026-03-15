package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;

import static moe.wolfgirl.probejs.lang.typescript.code.type.Types.*;

public class FluidProbeJS implements NeoAddonProbeJS {
    @Override
    public void assignType(ScriptDump scriptDump) {
        var fluid = or(primitive("`${Special.Fluid}`"), primitive("`.${Special.Fluid}`"), primitive("`#${Special.FluidTag}`"), primitive("`@${Special.Mod}`"), type(FluidCollectionWrapper.class).asArray());
        scriptDump.assignType(FluidCollectionWrapper.class, fluid);
    }
}
