package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.probejs;

import de.dasbabypixel.gamestages.common.data.FluidCollection;
import de.dasbabypixel.gamestages.common.data.ItemCollection;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.RegisterEventJS;
import moe.wolfgirl.probejs.lang.java.clazz.ClassPath;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import moe.wolfgirl.probejs.lang.typescript.TypeScriptFile;
import moe.wolfgirl.probejs.lang.typescript.code.ImportInfo;
import moe.wolfgirl.probejs.lang.typescript.code.member.ClassDecl;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;

import java.util.Map;
import java.util.Objects;

import static moe.wolfgirl.probejs.lang.typescript.code.type.Types.*;

public class StagesProbeJSPlugin extends ProbeJSPlugin {
    @Override
    public void assignType(ScriptDump scriptDump) {
        var item = or(primitive("`${Special.Item}`"), primitive("`.${Special.Item}`"), primitive("`#${Special.ItemTag}`"), primitive("`@${Special.Mod}`"), type(ItemCollection.class).asArray());
        scriptDump.assignType(ItemCollection.class, item);
        var fluid = or(primitive("`${Special.Fluid}`"), primitive("`.${Special.Fluid}`"), primitive("`#${Special.FluidTag}`"), primitive("`@${Special.Mod}`"), type(FluidCollection.class).asArray());
        scriptDump.assignType(FluidCollection.class, fluid);
    }

    @Override
    public void modifyClasses(ScriptDump scriptDump, Map<ClassPath, TypeScriptFile> globalClasses) {
        var registerEventJS = file(globalClasses, RegisterEventJS.class);
        for (var method : findCode(registerEventJS).methods) {
            switch (method.name) {
                case "restrictItems" -> method.params.get(1).type = type(ItemCollection.class).asArray();
                case "restrictFluids" -> method.params.get(1).type = type(FluidCollection.class).asArray();
                case "mods" -> method.params.get(0).type = primitive("`${Special.Mod}`").asArray();
                case "items" -> method.params.get(0).type = type(ItemCollection.class).asArray();
                case "fluids" -> method.params.get(0).type = type(FluidCollection.class).asArray();
            }
        }
        registerEventJS.declaration.addClass(ImportInfo.type(new ClassPath(ItemCollection.class)));
        registerEventJS.declaration.addClass(ImportInfo.type(new ClassPath(FluidCollection.class)));
    }

    private TypeScriptFile file(Map<ClassPath, TypeScriptFile> globalClasses, Class<?> cls) {
        return Objects.requireNonNull(findClassFile(globalClasses, cls));
    }

    private ClassDecl findCode(TypeScriptFile file) {
        return file.findCode(ClassDecl.class).orElseThrow();
    }
}
