package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.manager.immutable.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ClientGameStageManager;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class JEIAddon implements NeoAddon {
    public static final JEIAddon ADDON = new JEIAddon();

    private JEIAddon() {
        COMPILE_ALL_POST_EVENT.addListener(this::handle);
    }

    private void handle(CompileAllPostEvent event) {
        var recompilationTask = event.playerCompilationTask();
        var instance = recompilationTask.manager();
        var stages = recompilationTask.stages();
        if (instance instanceof ClientGameStageManager) {
            // Only execute JEI logic when the manager is for the client
            // In single-player there is a server and client manager
            singleRefreshAll(instance, stages);

            for (var addon : StagesJEIPlugin.addons()) {
                addon.postCompileAll(instance, stages);
            }
        }
    }

    public void singleRefreshAll(AbstractGameStageManager<?> instance, BaseStages stages) {
        for (var addon : StagesJEIPlugin.addons()) {
            addon.singleRefreshAll(instance, stages);
        }
    }
}
