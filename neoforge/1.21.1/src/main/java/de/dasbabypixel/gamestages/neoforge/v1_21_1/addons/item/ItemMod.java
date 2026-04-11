package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.BuildConstants;
import net.neoforged.fml.common.Mod;
import org.jspecify.annotations.NullMarked;

import static de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonManager.registerAddon;

@Mod(BuildConstants.MOD_ID)
@NullMarked
public class ItemMod {
    public ItemMod() {
        registerAddon("item", NeoItemAddon::new);
    }
}
