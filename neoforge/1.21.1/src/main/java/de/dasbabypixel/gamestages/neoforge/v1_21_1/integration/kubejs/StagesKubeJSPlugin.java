package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.restriction.Restrictions;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonGameContent;
import de.dasbabypixel.gamestages.neoforge.integration.Mods;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonKJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonManager;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.StageEvents;
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

import java.util.HashMap;
import java.util.Map;

public class StagesKubeJSPlugin implements KubeJSPlugin {
    private final Map<NeoAddon, NeoAddonKJS> addonMap = new HashMap<>();
    private boolean populated = false;

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
    public void registerBindings(BindingRegistry bindings) {
        asserLoaded();

        bindings.add("GameStage", GameStage.class);
        bindings.add("Restrictions", Restrictions.class);
        bindings.add("destructurable", new BaseFunction(bindings.scope(), null) {
            @Override
            public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                return destructurableImpl(cx, scope, args);
            }
        });
    }

    @Override
    public void registerTypeWrappers(TypeWrapperRegistry registry) {
        asserLoaded();
        if (registry.scriptType() == ScriptType.SERVER) {

            var anyContentParser = new JSParserBase();
            registry.register(CollectionWrapper.class, (ContextFromFunction<CollectionWrapper>) (context, o) -> new CollectionWrapper(anyContentParser.parse(context, o)));
            registry.register(ModContentWrapper.class, (ContextFromFunction<ModContentWrapper>) (context, o) -> {
                var mod = o.toString();
                return new ModContentWrapper(new CommonGameContent.Mod(mod));
            });
            registry.register(GameContent.class, (ContextFromFunction<GameContent>) (context, o) -> {
                if (o instanceof GameContent g) return g;
                if (o instanceof ContentWrapper w) return w.content();
                throw new ClassCastException("Cannot convert " + o.getClass().getName() + " to GameContent");
            });

            for (var value : addonMap().values()) {
                value.registerTypeWrappers(registry);
            }
        }
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        asserLoaded();
        registry.register(StageEvents.GROUP);
    }

    @Override
    public void beforeScriptsLoaded(ScriptManager manager) {
        asserLoaded();
    }

    @Override
    public void afterScriptsLoaded(ScriptManager manager) {
        asserLoaded();
    }

    private Scriptable destructurableImpl(Context cx, Scriptable scope, Object[] args) {
        if (args.length == 0) {
            throw Context.reportRuntimeError("destructurable(event): missing event parameter", cx);
        }

        Object event = args[0];

        Scriptable in = cx.toObject(event, scope);
        Scriptable out = cx.newObject(scope);

        for (Object idObj : in.getIds(cx)) {
            String id = idObj.toString();
            Object val = in.get(cx, id, in);

            if (val instanceof Function f) {
                // bind method to original 'in'
                Function bound = new BaseFunction(scope, null) {
                    @Override
                    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                        return f.call(cx, scope, in, args);
                    }
                };
                out.put(cx, id, out, bound);
            }
        }

        return out;
    }

}
