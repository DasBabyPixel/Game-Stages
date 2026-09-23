package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs;

import de.dasbabypixel.gamestages.common.data.GameContentRegistry;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.restriction.Restrictions;
import de.dasbabypixel.gamestages.neoforge.integration.Mods;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonKJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonManager;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.StageEvents;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.GameCollectionJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.GameCollectionTypeJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.ModIdJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.ModIdJSImpl;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi.TypedGameCollectionJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.listener.KJSListeners;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry.ContextFromFunction;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Function;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class StagesKubeJSPlugin implements KubeJSPlugin {
    private static final Map<GameContentRegistry.Entry<?, ?, ?, ?>, JSContext.TypeEntry> TYPE_ENTRY_MAP = new HashMap<>();
    private final Map<NeoAddon, NeoAddonKJS> addonMap = new HashMap<>();
    private boolean populated = false;

    public static TypeInfo typedCollection(GameContentRegistry.Entry<?, ?, ?, ?> typeEntry) {
        return Objects.requireNonNull(TypeInfo.of(TypedGameCollectionJS.class));
    }

    public static JSContext.TypeEntry getTypeEntry(GameContentRegistry.Entry<?, ?, ?, ?> typeEntry) {
        return Objects.requireNonNull(TYPE_ENTRY_MAP.get(typeEntry));
    }

    public static void register(GameContentRegistry.Entry<?, ?, ?, ?> typeEntry, JSContext.TypedContentParser parser) {
        TYPE_ENTRY_MAP.put(typeEntry, new JSContext.TypeEntry(typeEntry, parser));
    }

    private void asserLoaded() {
        if (!Mods.KUBEJS.isLoaded()) {
            throw new IllegalStateException("KubeJS must be loaded at this point");
        }
    }

    public Map<NeoAddon, NeoAddonKJS> addonMap() {
        if (!populated) {
            for (var addon : NeoAddonManager.instance().addons()) {
                addonMap.put(addon, addon.createKubeJSSupport());
            }
            populated = true;
        }
        return addonMap;
    }

    @Override
    public void init() {
        asserLoaded();
        KJSListeners.register();
    }

    @Override
    public void registerBindings(@Nullable BindingRegistry bindings) {
        Objects.requireNonNull(bindings);
        asserLoaded();

        bindings.add("GameStage", GameStage.class);
        bindings.add("Restrictions", Restrictions.class);
        bindings.add("destructurable", new BaseFunction(Objects.requireNonNull(bindings.scope()), null) {
            @Override
            public Object call(@Nullable Context cx, @Nullable Scriptable scope, @Nullable Scriptable thisObj, Object @Nullable [] args) {
                return destructurableImpl(Objects.requireNonNull(cx), Objects.requireNonNull(scope), Objects.requireNonNull(args));
            }
        });
    }

    @Override
    public void registerTypeWrappers(@Nullable TypeWrapperRegistry registry) {
        Objects.requireNonNull(registry);
        asserLoaded();
        if (registry.scriptType() == ScriptType.SERVER) {

            var anyContentParser = new JSParserBase();
            registry.register(GameCollectionJS.class, (ContextFromFunction<GameCollectionJS>) (context, o) -> anyContentParser.parse(Objects.requireNonNull(context), Objects.requireNonNull(o)));

            registry.register(GameCollectionTypeJS.class, (ContextFromFunction<GameCollectionTypeJS>) (context, o) -> {
                Objects.requireNonNull(context);
                var cx = JSContext.instance(context);
                return cx
                        .get(JSContext.Attributes.CONTENT_TYPES)
                        .typeById((String) Objects.requireNonNull(context.jsToJava(o, TypeInfo.STRING)));
            });

            for (var value : addonMap().values()) {
                value.registerTypeWrappers(registry);
            }

            registry.register(ModIdJS.class, (ContextFromFunction<ModIdJS>) (context, o) -> new ModIdJSImpl((String) Objects
                    .requireNonNull(context)
                    .jsToJava(o, TypeInfo.STRING)));
        }
    }

    @Override
    public void registerEvents(@Nullable EventGroupRegistry registry) {
        Objects.requireNonNull(registry);
        asserLoaded();
        registry.register(StageEvents.GROUP);
    }

    @Override
    public void beforeScriptsLoaded(@Nullable ScriptManager manager) {
        asserLoaded();
    }

    @Override
    public void afterScriptsLoaded(@Nullable ScriptManager manager) {
        asserLoaded();
    }

    private Scriptable destructurableImpl(Context cx, Scriptable scope, Object[] args) {
        if (args.length == 0) {
            throw Objects.requireNonNull(Context.reportRuntimeError("destructurable(event): missing event parameter", cx));
        }

        Object event = args[0];

        Scriptable in = Objects.requireNonNull(cx.toObject(event, scope));
        Scriptable out = Objects.requireNonNull(cx.newObject(scope));

        for (Object idObj : Objects.requireNonNull(in.getIds(cx))) {
            String id = Objects.requireNonNull(idObj).toString();
            Object val = in.get(cx, id, in);

            if (val instanceof Function f) {
                // bind method to original 'in'
                Function bound = new BaseFunction(scope, null) {
                    @Override
                    public @Nullable Object call(@Nullable Context cx, @Nullable Scriptable scope, @Nullable Scriptable thisObj, Object @Nullable [] args) {
                        return f.call(cx, scope, in, args);
                    }
                };
                out.put(cx, id, out, bound);
            }
        }

        return out;
    }

}
