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
}
