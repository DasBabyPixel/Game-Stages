package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.probejs;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.EventRegistryImpl;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonManager;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.ModContentWrapper;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;
import moe.wolfgirl.probejs.typescript.ClassPath;
import moe.wolfgirl.probejs.typescript.Documents;
import moe.wolfgirl.probejs.typescript.base.AliasRegistrar;
import moe.wolfgirl.probejs.typescript.base.DocumentRegistrar;
import moe.wolfgirl.probejs.typescript.document.ClassDecl;
import moe.wolfgirl.probejs.typescript.document.Types;
import moe.wolfgirl.probejs.typescript.document.base.Code;
import moe.wolfgirl.probejs.typescript.document.members.MethodDecl;
import moe.wolfgirl.probejs.typescript.document.members.ParamDecl;
import moe.wolfgirl.probejs.typescript.document.types.VariableType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
    public void modifyClasses(Documents.@Nullable ClassAccessor classDocuments) {
        Objects.requireNonNull(classDocuments);
        var typeConverter = Objects.requireNonNull(classDocuments.converter);

        for (var entry : Objects.requireNonNull(eventRegistry).types().entrySet()) {
            var cls = Objects.requireNonNull(entry).getKey();
            var eventType = entry.getValue();

            var decl = (ClassDecl) Objects.requireNonNull(classDocuments.getDocument(cls));

            var members = new ArrayList<Code>();
            for (var entry2 : eventType.functions().entrySet()) {
                var name = Objects.requireNonNull(entry2).getKey();
                var function = entry2.getValue();
                var descriptor = function.descriptor();
                var returnType = typeConverter.convertType(descriptor.returnType().probeType());

                var variableTypes = new ArrayList<VariableType>();
                var params = new ArrayList<ParamDecl>();
                var nameId = 0;
                for (var i = 0; i < descriptor.parameters().length; i++) {
                    var param = descriptor.parameters()[i];
                    var last = i == descriptor.parameters().length - 1;
                    var varArg = last && descriptor.varArgs();
                    var paramName = "arg" + (nameId++);
                    var paramType = varArg ? typeConverter.convertType(param
                                                                       .probeType()
                                                                       .componentType()) : typeConverter.convertType(param.probeType());
                    Objects.requireNonNull(paramType).markAsInput();
                    params.add(new ParamDecl(paramName, paramType, varArg, false));
                }

                var m = new MethodDecl(name, variableTypes, params, returnType, false);
                members.add(m);
            }
            decl = new ClassDecl(decl.export, decl.kind, decl.identifier, Types.OBJECT, List.of(Types.clazz(KubeEvent.class)), List.of(), members);
            classDocuments.addClassDocument(new ClassPath(cls), decl);
        }
    }

    @Override
    public Set<Class<?>> provideClassForDiscovery() {
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
    public void addTypeAlias(@Nullable AliasRegistrar registrar) {
        Objects.requireNonNull(registrar);

        var mod = Types.raw("`${Special.Mod}`").asArray();
        registrar.addInputAlias(ModContentWrapper.class, mod);

        for (var addon : addonMap().values()) {
            addon.addTypeAlias(registrar);
        }
    }

    @Override
    public void addSidedDocuments(@Nullable DocumentRegistrar registrar) {
        Objects.requireNonNull(registrar);

        registrar.addGlobal(ClassPath.sided(ScriptType.SERVER, "gamestages.destructurable"), Types.raw("""
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
                """));
    }
}
