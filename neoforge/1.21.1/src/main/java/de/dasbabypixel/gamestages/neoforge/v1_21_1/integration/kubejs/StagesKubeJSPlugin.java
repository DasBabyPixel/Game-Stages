package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs;

import de.dasbabypixel.gamestages.common.integration.kubejs.GameStagesPlugin;
import de.dasbabypixel.gamestages.common.integration.kubejs.binding.KJSBindingRegistry;
import de.dasbabypixel.gamestages.common.integration.kubejs.event.KJSEventGroupRegistry;
import de.dasbabypixel.gamestages.neoforge.integration.Mods;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.listener.KJSListeners;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.ScriptManager;

@SuppressWarnings({"unused", "DataFlowIssue"})
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
        GameStagesPlugin.registerBindings((KJSBindingRegistry) (Object) bindings);
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        asserLoaded();
        GameStagesPlugin.registerEvents((KJSEventGroupRegistry) registry);
    }

    @Override
    public void beforeScriptsLoaded(ScriptManager manager) {
        asserLoaded();
        GameStagesPlugin.beforeScripts(KJSUtil.convert(manager.scriptType));
    }

    @Override
    public void afterScriptsLoaded(ScriptManager manager) {
        asserLoaded();
        GameStagesPlugin.afterScripts(KJSUtil.convert(manager.scriptType));
    }
}
