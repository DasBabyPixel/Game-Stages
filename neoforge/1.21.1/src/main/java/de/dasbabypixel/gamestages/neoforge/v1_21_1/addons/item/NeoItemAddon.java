package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.VItemAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonJEI;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonKJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class NeoItemAddon extends VItemAddon implements NeoAddon {
    @Override
    public NeoAddonKJS createKubeJSSupport() {
        return new ItemKJS();
    }

    @Override
    public NeoAddonJEI createJEISupport() {
        return new ItemJEI();
    }

    @Override
    public NeoAddonProbeJS createProbeJSSupport() {
        return new ItemProbeJS();
    }
}
