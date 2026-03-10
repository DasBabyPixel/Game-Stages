package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs;

import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.ItemCollection;
import de.dasbabypixel.gamestages.common.data.restriction.Restrictions;
import de.dasbabypixel.gamestages.neoforge.integration.Mods;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.StageEvents;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.listener.KJSListeners;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.probejs.StagesProbeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Function;
import dev.latvian.mods.rhino.Scriptable;

import java.lang.invoke.MethodHandles;
import java.util.List;

@SuppressWarnings("unchecked")
public class StagesKubeJSPlugin implements KubeJSPlugin {
    static {
        try {
            if (Mods.PROBEJS.isLoaded()) {
                var lookup = MethodHandles.privateLookupIn(KubeJSPlugins.class, MethodHandles.lookup());
                var varHandle = lookup.findStaticVarHandle(KubeJSPlugins.class, "LIST", List.class);
                var list = (List<KubeJSPlugin>) varHandle.get();
                list.add(new StagesProbeJSPlugin());
            }
        } catch (IllegalAccessException | NoSuchFieldException t) {
            throw new RuntimeException(t);
        }
    }

    private void asserLoaded() {
        if (!Mods.KUBEJS.isLoaded()) {
            throw new IllegalStateException("KubeJS must be loaded at this point");
        }
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
        bindings.add("ItemCollection", ItemCollection.class);
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
