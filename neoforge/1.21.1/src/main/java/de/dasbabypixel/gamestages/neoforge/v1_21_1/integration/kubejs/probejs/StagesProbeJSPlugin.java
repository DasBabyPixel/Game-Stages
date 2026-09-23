package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.probejs;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.GameContentRegistry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.EventRegistryImpl;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonManager;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.EventType;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.GameCollectionJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.GameCollectionTypeJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.ModIdJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.TypedGameCollectionJS;
import dev.latvian.mods.kubejs.event.KubeEvent;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;
import moe.wolfgirl.probejs.plugin.builtins.alias.SpecialTypes;
import moe.wolfgirl.probejs.typescript.ClassPath;
import moe.wolfgirl.probejs.typescript.Documents;
import moe.wolfgirl.probejs.typescript.base.AliasRegistrar;
import moe.wolfgirl.probejs.typescript.base.DocumentRegistrar;
import moe.wolfgirl.probejs.typescript.document.ClassDecl;
import moe.wolfgirl.probejs.typescript.document.Members;
import moe.wolfgirl.probejs.typescript.document.TypeDecl;
import moe.wolfgirl.probejs.typescript.document.Types;
import moe.wolfgirl.probejs.typescript.document.base.Code;
import moe.wolfgirl.probejs.typescript.document.base.KindAware;
import moe.wolfgirl.probejs.typescript.document.base.Type;
import moe.wolfgirl.probejs.typescript.document.members.MethodDecl;
import moe.wolfgirl.probejs.typescript.document.members.ParamDecl;
import moe.wolfgirl.probejs.typescript.document.types.ClassType;
import moe.wolfgirl.probejs.typescript.document.types.ParamType;
import moe.wolfgirl.probejs.typescript.document.types.VariableType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@NullMarked
public class StagesProbeJSPlugin extends ProbeJSPlugin {
    public static final ClassTransformerRegistry transformerRegistry = new ClassTransformerRegistry();
    private static final ClassPath CLASS_PATH_GAME_STAGES = new ClassPath("gamestages");
    private static final ClassPath CLASS_PATH_CONTENT_TYPE_REGISTRY = Objects.requireNonNull(CLASS_PATH_GAME_STAGES.append("ContentTypeRegistry"));
    private static final ClassPath CLASS_PATH_TYPE_COMPLETIONS = Objects.requireNonNull(CLASS_PATH_GAME_STAGES.append("TypeCompletions"));
    private static final ClassPath CLASS_PATH_GAME_COLLECTION_USING_ONLY = Objects.requireNonNull(CLASS_PATH_GAME_STAGES.append("GameCollectionUsingOnly"));
    public static @Nullable EventRegistryImpl eventRegistry;

    static {
        registerTransformers();
    }

    private final Map<NeoAddon, NeoAddonProbeJS> addonMap = new HashMap<>();
    private boolean populated;

    public static ParamType typedCollection(GameContentRegistry.Entry<?, ?, ?, ?> typeEntry) {
        return typedCollection(contentType(typeEntry));
    }

    public static ParamType typedCollection(Type contentType) {
        return Objects.requireNonNull(Types.clazz(TypedGameCollectionJS.class).withParams(contentType));
    }

    public static ClassType contentType(GameContentRegistry.Entry<?, ?, ?, ?> typeEntry) {
        return Types.clazz(contentTypeClassPath(typeEntry));
    }

    public static ClassType typedCompletions(GameContentRegistry.Entry<?, ?, ?, ?> typeEntry) {
        return Types.clazz(typedCompletionsClassPath(typeEntry));
    }

    public static ClassPath typedCompletionsClassPath(GameContentRegistry.Entry<?, ?, ?, ?> typeEntry) {
        return Objects.requireNonNull(CLASS_PATH_GAME_STAGES.append("completions", "$completions_" + typeEntry.id()));
    }

    public static ClassPath contentTypeClassPath(GameContentRegistry.Entry<?, ?, ?, ?> typeEntry) {
        return Objects.requireNonNull(CLASS_PATH_GAME_STAGES.append("types", "$" + typeEntry.id()));
    }

    public static Type collectionUsingOnly(GameContentRegistry.Entry<?, ?, ?, ?> typeEntry) {
        return collectionUsingOnly(contentType(typeEntry));
    }

    public static Type collectionUsingOnly(Type paramType) {
        return Objects.requireNonNull(Types.clazz(CLASS_PATH_GAME_COLLECTION_USING_ONLY).withParams(paramType));
    }

    @SuppressWarnings({"DataFlowIssue", "CodeBlock2Expr"})
    private static void registerTransformers() {
        transformerRegistry.register(GameCollectionTypeJS.class, classDecl -> {
            classDecl.typeParams.add(Types.variable("Type"));
        });
        {
            var variable = Types.variable("Type");
            transformerRegistry.register(TypedGameCollectionJS.class, classDecl -> {
                classDecl.typeParams.add(variable);
            });
            transformerRegistry.register(TypedGameCollectionJS.class, (classDecl, methodDecl) -> {
                methodDecl.returnType = Types.parameterized(Types.clazz(GameCollectionTypeJS.class), variable);
            }, "type");
            transformerRegistry.register(TypedGameCollectionJS.class, (classDecl, methodDecl) -> {
                methodDecl.returnType = Types.parameterized(Types.clazz(TypedGameCollectionJS.class), variable);
                methodDecl.params.getFirst().typeInfo = collectionUsingOnly(variable).asArray();
            }, "only", "except");
        }
        {
            transformerRegistry.register(GameCollectionJS.class, (classDecl, methodDecl) -> {
                var variable = Types.variable("Type", Types.wrapped("keyof %s", Types.clazz(CLASS_PATH_CONTENT_TYPE_REGISTRY)));
                methodDecl.typeParams.add(variable);
                methodDecl.params.getFirst().typeInfo = variable;
                methodDecl.returnType = Types.parameterized(Types.clazz(TypedGameCollectionJS.class), Types.wrapped("%s[Type]", Types.clazz(CLASS_PATH_CONTENT_TYPE_REGISTRY)));
            }, "filterType");
        }
    }

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
    public void transformClass(Documents.@Nullable ClassDocument document) {
        addonMap(); // Initialize addons
        Objects.requireNonNull(document);
        transformerRegistry.transform(Objects.requireNonNull(Objects
                .requireNonNull(document.classInfo())
                .clazz()), Objects.requireNonNull(document.document()));
    }

    @Override
    public void modifyClasses(Documents.@Nullable ClassAccessor classDocuments) {
        addonMap(); // Initialize addons

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
                var returnType = typeConverter.convertType(descriptor.returnType());

                var variableTypes = new ArrayList<VariableType>();
                var params = new ArrayList<ParamDecl>();
                var nameId = 0;
                for (var i = 0; i < descriptor.parameters().size(); i++) {
                    var param = descriptor.parameters().get(i);
                    var last = i == descriptor.parameters().size() - 1;
                    var varArg = last && descriptor.varArgs();
                    var paramName = "arg" + (nameId++);
                    var paramTypeRaw = param.typeInfo();
//                    var paramType = typeConverter.convertType(varArg ? paramTypeRaw.componentType() : paramTypeRaw);
                    var paramType = typeConverter.convertType(paramTypeRaw);
                    Objects.requireNonNull(paramType).markAsInput();
                    params.add(new ParamDecl(paramName, paramType, varArg, false));
                }

                var m = new MethodDecl(name, variableTypes, params, returnType, false);
                members.add(m);
            }
            decl = new ClassDecl(decl.export, decl.kind, decl.identifier, Types.OBJECT, new ArrayList<>(List.of(Types.clazz(KubeEvent.class))), new ArrayList<>(), members);
            transformerRegistry.transform(cls, decl);

            classDocuments.addClassDocument(new ClassPath(cls), decl);
        }

        addDocuments(Documents.INSTANCE);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public Set<Class<?>> provideClassForDiscovery() {
        var eventClasses = new HashSet<Class<?>>();
        for (var entry : Objects.requireNonNull(eventRegistry).types().entrySet()) {
            eventClasses.add(entry.getKey());
            for (EventType.Function<?> function : entry.getValue().functions().values()) {
                eventClasses.add(function.descriptor().returnType().asClass());
                for (var i = 0; i < function.descriptor().parameters().size(); i++) {
                    var parameter = function.descriptor().parameters().get(i);
                    var last = i == function.descriptor().parameters().size() - 1;
                    if (last && function.descriptor().varArgs()) {
                        eventClasses.add(parameter.typeInfo().asClass().componentType());
                    } else {
                        eventClasses.add(parameter.typeInfo().asClass());
                    }
                }
            }
        }
        return eventClasses;
    }

    @Override
    public void addTypeAlias(@Nullable AliasRegistrar registrar) {
        Objects.requireNonNull(registrar);

        registrar.addInputAlias(ModIdJS.class, SpecialTypes.MOD_ID);

        for (var addon : addonMap().values()) {
            addon.addTypeAlias(registrar);
        }
    }

    @Override
    public void addSidedDocuments(@Nullable DocumentRegistrar registrar) {
    }

    @Override
    public void addSpecialDocuments(@Nullable DocumentRegistrar registrar) {
        Objects.requireNonNull(registrar);

        registrar.addGlobal(ClassPath.special("gamestages.destructurable"), Types.raw("""
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

    @SuppressWarnings({"DataFlowIssue", "SameParameterValue"})
    private void addDocuments(Documents registrar) {
        Objects.requireNonNull(registrar);

        var gameContentRegistry = CommonInstances.gameContentRegistry;
        {
            for (var entry : gameContentRegistry.entries()) {
                var classPath = contentTypeClassPath(entry);
                var typeClass = Members
                        .clazz(classPath)
                        .kind(KindAware.Kind.INTERFACE)
                        .field("_brand", Types.literal("brand_" + entry.id()))
                        .build();
                registrar.addDocument(classPath, typeClass);

                var completionsClassPath = typedCompletionsClassPath(entry);
                var completionsClass = Members
                        .clazz(completionsClassPath)
                        .kind(KindAware.Kind.INTERFACE)
                        .implementsType(Types.clazz(GameCollectionJS.class))
                        .build();
                registrar.addDocument(completionsClassPath, completionsClass);
            }
        }
        {
            var contentTypeRegistry = Members.clazz(CLASS_PATH_CONTENT_TYPE_REGISTRY).kind(KindAware.Kind.INTERFACE);
            for (var entry : gameContentRegistry.entries()) {
                contentTypeRegistry.field(entry.id(), contentType(entry));
            }
            registrar.addDocument(CLASS_PATH_CONTENT_TYPE_REGISTRY, contentTypeRegistry.build());
        }
        {
            var variable = Types.variable("Type");
            Type instanceOfChain = Types.NEVER;
            for (var entry : gameContentRegistry.entries()) {
                instanceOfChain = new InstanceTestType(variable, contentType(entry), typedCompletions(entry).markAsInput(), instanceOfChain);
            }
            var typeCompletions = new TypeDecl(CLASS_PATH_TYPE_COMPLETIONS, List.of(variable), instanceOfChain, true);
            registrar.addDocument(CLASS_PATH_TYPE_COMPLETIONS, typeCompletions);
        }
        {
            var variable = Types.variable("Type");
            var collectionUsingOnly = new TypeDecl(CLASS_PATH_GAME_COLLECTION_USING_ONLY, List.of(variable), Types.union(Types.clazz(GameCollectionJS.class), Types
                    .clazz(CLASS_PATH_TYPE_COMPLETIONS)
                    .withParams(variable)), true);
            registrar.addDocument(CLASS_PATH_GAME_COLLECTION_USING_ONLY, collectionUsingOnly);
        }
    }

    private static class InstanceTestType extends Type {
        private final Type baseType;
        private final Type matchType;
        private final Type matchSuccessType;
        private final Type matchFailType;

        public InstanceTestType(Type baseType, Type matchType, Type matchSuccessType, Type matchFailType) {
            this.baseType = baseType;
            this.matchType = matchType;
            this.matchSuccessType = matchSuccessType;
            this.matchFailType = matchFailType;
        }

        @Override
        public Set<ClassPath> getImports() {
            var set = new HashSet<>(Objects.requireNonNull(baseType.getImports()));
            set.addAll(Objects.requireNonNull(matchType.getImports()));
            set.addAll(Objects.requireNonNull(matchSuccessType.getImports()));
            set.addAll(Objects.requireNonNull(matchFailType.getImports()));
            return set;
        }

        @Override
        public List<String> format(int indent) {
            return List.of("%s%s extends %s ? %s : %s".formatted(" ".repeat(indent), baseType.first(), matchType.first(), matchSuccessType.first(), matchFailType.first()));
        }

        @Override
        public Collection<Code> getContainedTypes() {
            return List.of(baseType, matchType, matchSuccessType, matchFailType);
        }
    }
}
