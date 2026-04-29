package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.probejs;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.EventRegistryImpl;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonManager;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.ModContentWrapper;
import dev.latvian.mods.kubejs.event.KubeEvent;
import moe.wolfgirl.probejs.lang.java.clazz.ClassPath;
import moe.wolfgirl.probejs.lang.transpiler.Transpiler;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import moe.wolfgirl.probejs.lang.typescript.TypeScriptFile;
import moe.wolfgirl.probejs.lang.typescript.code.Code;
import moe.wolfgirl.probejs.lang.typescript.code.member.ClassDecl;
import moe.wolfgirl.probejs.lang.typescript.code.member.MethodDecl;
import moe.wolfgirl.probejs.lang.typescript.code.member.ParamDecl;
import moe.wolfgirl.probejs.lang.typescript.code.type.TSVariableType;
import moe.wolfgirl.probejs.lang.typescript.code.type.Types;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static moe.wolfgirl.probejs.lang.typescript.code.type.Types.primitive;

@NullMarked
public class StagesProbeJSPlugin extends ProbeJSPlugin {
    public static @Nullable EventRegistryImpl eventRegistry;
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
        globalClasses.remove(new ClassPath(SingletonArgumentInfo.Template.class));

        var typeConverter = Objects.requireNonNull(Objects.requireNonNull(scriptDump.transpiler).typeConverter);
        for (var entry : Objects.requireNonNull(eventRegistry).types().entrySet()) {
            var cls = Objects.requireNonNull(entry).getKey();
            var eventType = entry.getValue();

            var classFile = Objects.requireNonNull(findClassFile(globalClasses, cls));
            var newCode = new ArrayList<Code>();
            for (var code : Objects.requireNonNull(classFile.codeList)) {
                if (code instanceof ClassDecl decl) {
                    decl = new ClassDecl(decl.name, null, List.of(Types.type(KubeEvent.class)), List.of());
                    newCode.add(decl);

                    for (var entry2 : eventType.functions().entrySet()) {
                        var name = Objects.requireNonNull(entry2).getKey();
                        var function = entry2.getValue();
                        var descriptor = function.descriptor();
                        var returnType = typeConverter.convertType(descriptor.returnType().probeType());

                        var variableTypes = new ArrayList<TSVariableType>();
                        var params = new ArrayList<ParamDecl>();
                        var nameId = 0;
                        for (var i = 0; i < descriptor.parameters().length; i++) {
                            var param = descriptor.parameters()[i];
                            var last = i == descriptor.parameters().length - 1;
                            var varArg = last && descriptor.varArgs();
                            var paramName = "arg" + (nameId++);
                            var paramType = varArg ? typeConverter.convertType(param.probeType()
                                                                               .componentType()) : typeConverter.convertType(param.probeType());
                            params.add(new ParamDecl(paramName, paramType, varArg, false));
                        }

                        var m = new MethodDecl(name, variableTypes, params, returnType);
                        Objects.requireNonNull(decl.methods).add(m);
                    }
                }
            }
            classFile.codeList.clear();
            for (var code : newCode) {
                classFile.addCode(code);
            }
        }
    }

    @Override
    public Set<Class<?>> provideJavaClass(ScriptDump scriptDump) {
        var eventClasses = new HashSet<Class<?>>();
        for (var entry : Objects.requireNonNull(eventRegistry).types().entrySet()) {
            eventClasses.add(Objects.requireNonNull(entry).getKey());
            for (var function : entry.getValue().functions().values()) {
                eventClasses.add(Objects.requireNonNull(function.descriptor().returnType().probeType().asClass()));
                for (var i = 0; i < function.descriptor().parameters().length; i++) {
                    var parameter = function.descriptor().parameters()[i];
                    var last = i == function.descriptor().parameters().length - 1;
                    if (last && function.descriptor().varArgs()) {
                        eventClasses.add(Objects.requireNonNull(parameter.probeType().asClass()).componentType());
                    } else {
                        eventClasses.add(Objects.requireNonNull(parameter.probeType().asClass()));
                    }
                }
            }
        }
        return eventClasses;
    }

    @Override
    public void denyTypes(Transpiler transpiler) {
    }

    @Override
    public void assignType(ScriptDump scriptDump) {
        var mod = primitive("`${Special.Mod}`").asArray();
        scriptDump.assignType(ModContentWrapper.class, mod);

        for (var addon : addonMap().values()) {
            addon.assignType(scriptDump);
        }
    }

    @Override
    public void addGlobals(ScriptDump scriptDump) {
        var destructurable = primitive("""
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
