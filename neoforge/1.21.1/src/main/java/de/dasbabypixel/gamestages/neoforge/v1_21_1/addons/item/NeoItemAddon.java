package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.VItemAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonKJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.listener.ClientEventListener;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei.JEIIntegration;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class NeoItemAddon extends VItemAddon implements NeoAddon {
    public NeoItemAddon() {
        if (FMLEnvironment.dist.isClient()) {
            ClientEventListener.register();
        }
        JEIIntegration.INIT_JEI_SUPPORT_EVENT.addListener(this::initJEISupport);
    }

    @Override
    public NeoAddonKJS createKubeJSSupport() {
        return new ItemKJS();
    }


    private void initJEISupport(JEIIntegration.InitJEISupportEvent event) {
        ItemJEI.init();
    }

    @Override
    public NeoAddonProbeJS createProbeJSSupport() {
        return new ItemProbeJS();
    }
}
