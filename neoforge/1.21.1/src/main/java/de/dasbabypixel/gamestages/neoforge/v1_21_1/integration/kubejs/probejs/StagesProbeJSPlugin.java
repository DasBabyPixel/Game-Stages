package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.probejs;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.fluid.FluidCollectionWrapper;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.ItemCollectionWrapper;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.RecipeCollectionWrapper;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.ModCollectionWrapper;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventJSBase;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.RegisterEventJS;
import moe.wolfgirl.probejs.lang.java.clazz.ClassPath;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import moe.wolfgirl.probejs.lang.typescript.TypeScriptFile;
import moe.wolfgirl.probejs.lang.typescript.code.type.Types;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;

import java.util.Map;

import static moe.wolfgirl.probejs.lang.typescript.code.type.Types.*;

public class StagesProbeJSPlugin extends ProbeJSPlugin {
    @Override
    public void modifyClasses(ScriptDump scriptDump, Map<ClassPath, TypeScriptFile> globalClasses) {
        this.findClassFile(globalClasses, EventJSBase.class).codeList.clear();
        this.findClassFile(globalClasses, RegisterEventJS.class).codeList.clear();
    }

    @Override
    public void assignType(ScriptDump scriptDump) {
        var item = or(primitive("`${Special.Item}`"), primitive("`.${Special.Item}`"), primitive("`#${Special.ItemTag}`"), primitive("`@${Special.Mod}`"), type(ItemCollectionWrapper.class).asArray());
        scriptDump.assignType(ItemCollectionWrapper.class, item);
        var fluid = or(primitive("`${Special.Fluid}`"), primitive("`.${Special.Fluid}`"), primitive("`#${Special.FluidTag}`"), primitive("`@${Special.Mod}`"), type(FluidCollectionWrapper.class).asArray());
        scriptDump.assignType(FluidCollectionWrapper.class, fluid);
        var recipe = or(primitive("`${Special.RecipeId}`"), primitive("`@${Special.Mod}`"), type(RecipeCollectionWrapper.class).asArray());
        scriptDump.assignType(RecipeCollectionWrapper.class, recipe);
        var mod = or(primitive("`${Special.Mod}`"));
        scriptDump.assignType(ModCollectionWrapper.class, mod);
    }

    @Override
    public void addGlobals(ScriptDump scriptDump) {
        var destructurable = Types.primitive("""
                type FunctionKeys<T> = {
                  [K in keyof T]: T[K] extends (...args: any[]) => any ? K : never
                }[keyof T];
                
                function destructurable<T extends object>(
                  event: T
                ): Pick<T, FunctionKeys<T>> {
                  const out = {} as Pick<T, FunctionKeys<T>>;
                
                  for (const key in event) {
                    const value = event[key];
                    if (typeof value === "function") {
                      // Bind and preserve type
                      out[key as FunctionKeys<T>] = value.bind(event);
                    }
                  }
                
                  return out;
                }
                """);
        scriptDump.addGlobal("stages", destructurable);
    }
}
