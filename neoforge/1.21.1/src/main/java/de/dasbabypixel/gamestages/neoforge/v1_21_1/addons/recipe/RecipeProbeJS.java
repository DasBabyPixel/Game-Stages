package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import org.jspecify.annotations.NullMarked;

import static moe.wolfgirl.probejs.lang.typescript.code.type.Types.*;

@NullMarked
public class RecipeProbeJS implements NeoAddonProbeJS {
    @Override
    public void assignType(ScriptDump scriptDump) {
        var recipe = or(primitive("`${Special.RecipeId}`"), primitive("`@${Special.Mod}`"), type(RecipeCollectionWrapper.class).asArray());
        scriptDump.assignType(RecipeCollectionWrapper.class, recipe);
    }
}
