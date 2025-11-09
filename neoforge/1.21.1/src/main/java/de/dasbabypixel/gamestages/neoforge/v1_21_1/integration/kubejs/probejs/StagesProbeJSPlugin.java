package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.probejs;

import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonItemCollection;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.RegisterEventJS;
import moe.wolfgirl.probejs.lang.java.clazz.ClassPath;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import moe.wolfgirl.probejs.lang.typescript.TypeScriptFile;
import moe.wolfgirl.probejs.lang.typescript.code.member.ClassDecl;
import moe.wolfgirl.probejs.lang.typescript.code.type.Types;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;

import java.util.Map;

public class StagesProbeJSPlugin extends ProbeJSPlugin {
    @Override
    public void assignType(ScriptDump scriptDump) {
    }

    @Override
    public void modifyClasses(ScriptDump scriptDump, Map<ClassPath, TypeScriptFile> globalClasses) {
        for (var method : findCode(globalClasses, RegisterEventJS.class).methods) {
            if (method.name.equals("items")) {
                method.params.getFirst().type = Types
                        .or(Types.primitive("`${Special.Item}`"), Types.primitive("`.${Special.Item}`"), Types.primitive("`#${Special.ItemTag}`"), Types.primitive("`@${Special.Mod}`"))
                        .asArray();
            }
        }
        findCode(globalClasses, CommonItemCollection.class).methods.removeIf(m -> m.name.equals("serializer") || m.name.equals("self"));
    }

    private ClassDecl findCode(Map<ClassPath, TypeScriptFile> globalClasses, Class<?> cls) {
        return findClassFile(globalClasses, cls).findCode(ClassDecl.class).orElseThrow();
    }
}
