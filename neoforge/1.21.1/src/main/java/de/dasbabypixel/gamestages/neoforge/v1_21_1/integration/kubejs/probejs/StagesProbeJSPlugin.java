package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.probejs;

import de.dasbabypixel.gamestages.common.data.ItemCollection;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.RegisterEventJS;
import moe.wolfgirl.probejs.lang.java.clazz.ClassPath;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import moe.wolfgirl.probejs.lang.typescript.TypeScriptFile;
import moe.wolfgirl.probejs.lang.typescript.code.ImportInfo;
import moe.wolfgirl.probejs.lang.typescript.code.member.ClassDecl;
import moe.wolfgirl.probejs.lang.typescript.code.type.Types;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;

import java.util.Map;
import java.util.Objects;

public class StagesProbeJSPlugin extends ProbeJSPlugin {
    @Override
    public void assignType(ScriptDump scriptDump) {
        var item = Types.or(Types.primitive("`${Special.Item}`"), Types.primitive("`.${Special.Item}`"), Types.primitive("`#${Special.ItemTag}`"), Types.primitive("`@${Special.Mod}`"), Types
                .type(ItemCollection.class)
                .asArray());
        scriptDump.assignType(ItemCollection.class, item);
    }

    @Override
    public void modifyClasses(ScriptDump scriptDump, Map<ClassPath, TypeScriptFile> globalClasses) {
        var registerEventJS = file(globalClasses, RegisterEventJS.class);
        for (var method : findCode(registerEventJS).methods) {
            switch (method.name) {
                case "restrictItems" -> method.params.get(1).type = Types.type(ItemCollection.class).asArray();
                case "items" -> method.params.get(0).type = Types.type(ItemCollection.class).asArray();
            }
        }
        registerEventJS.declaration.addClass(ImportInfo.type(new ClassPath(ItemCollection.class)));
    }

    private TypeScriptFile file(Map<ClassPath, TypeScriptFile> globalClasses, Class<?> cls) {
        return Objects.requireNonNull(findClassFile(globalClasses, cls));
    }

    private ClassDecl findCode(TypeScriptFile file) {
        return file.findCode(ClassDecl.class).orElseThrow();
    }
}
