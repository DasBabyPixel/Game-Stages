package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ClientGameStageManager;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class JEIAddon implements NeoAddon {
    public static final JEIAddon ADDON = new JEIAddon();

    private JEIAddon() {
        CLIENT_RECOMPILE_PRE_EVENT.addListener(this::handle);
        CLIENT_RECOMPILE_POST_EVENT.addListener(this::handle);
    }

    private void handle(ClientRecompilePreEvent event) {
        var manager = event.newManager();
        var stages = event.stages();
        for (var addon : StagesJEIPlugin.addons()) {
            addon.preRecompileStages(manager, stages);
        }
    }

    private void handle(ClientRecompilePostEvent event) {
        var manager = event.newManager();
        var stages = event.stages();
        for (var addon : StagesJEIPlugin.addons()) {
            addon.postRecompileStages(manager, stages);
        }
    }

    public void jeiReloaded(ClientGameStageManager manager, BaseStages stages) {
        for (var addon : StagesJEIPlugin.addons()) {
            addon.jeiReloaded(manager, stages);
        }
    }
}
