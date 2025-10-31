package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs;

import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.GameStageReference;
import de.dasbabypixel.gamestages.common.data.ItemCollection;
import de.dasbabypixel.gamestages.neoforge.integration.Mods;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.StageEvents;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.listener.KJSListeners;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;

public class StagesKubeJSPlugin implements KubeJSPlugin {
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
        bindings.add("GameStageReference", GameStageReference.class);
        bindings.add("ItemCollection", ItemCollection.class);
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        asserLoaded();
        registry.register(StageEvents.GROUP);
    }

    @Override
    public void beforeScriptsLoaded(ScriptManager manager) {
        asserLoaded();
        if (manager.scriptType == ScriptType.SERVER) {
            System.out.println("Before scripts loaded");
        }
    }

    @Override
    public void afterScriptsLoaded(ScriptManager manager) {
        asserLoaded();
        if (manager.scriptType == ScriptType.SERVER) {
            System.out.println("After scripts loaded");
        }
    }
}
