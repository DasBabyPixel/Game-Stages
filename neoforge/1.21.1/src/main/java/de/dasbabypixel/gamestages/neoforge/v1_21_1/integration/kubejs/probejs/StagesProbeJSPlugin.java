package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.probejs;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonManager;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.ModCollectionWrapper;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventJSBase;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.RegisterEventJS;
import moe.wolfgirl.probejs.lang.java.clazz.ClassPath;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import moe.wolfgirl.probejs.lang.typescript.TypeScriptFile;
import moe.wolfgirl.probejs.lang.typescript.code.type.Types;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;

import java.util.HashMap;
import java.util.Map;

import static moe.wolfgirl.probejs.lang.typescript.code.type.Types.or;
import static moe.wolfgirl.probejs.lang.typescript.code.type.Types.primitive;

public class StagesProbeJSPlugin extends ProbeJSPlugin {
    private final Map<NeoAddon, NeoAddonProbeJS> addonMap = new HashMap<>();
    private boolean populated;

    private Map<NeoAddon, NeoAddonProbeJS> addonMap() {
        if (!populated) {
            populated = true;
            for (var addon : NeoAddonManager.instance().addons()) {
                addonMap.put(addon, addon.createProbeJSSupport());
            }
        }
        return addonMap;
    }

    @Override
    public void modifyClasses(ScriptDump scriptDump, Map<ClassPath, TypeScriptFile> globalClasses) {
        this.findClassFile(globalClasses, EventJSBase.class).codeList.clear();
        this.findClassFile(globalClasses, RegisterEventJS.class).codeList.clear();
    }

    @Override
    public void assignType(ScriptDump scriptDump) {
        var mod = or(primitive("`${Special.Mod}`"));
        scriptDump.assignType(ModCollectionWrapper.class, mod);

        for (var addon : addonMap().values()) {
            addon.assignType(scriptDump);
        }
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
