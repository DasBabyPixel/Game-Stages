package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.BuildConstants;
import net.neoforged.fml.common.Mod;

import static de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonManager.registerAddon;

@Mod(BuildConstants.MOD_ID)
public class RecipeMod {
    public RecipeMod() {
        registerAddon("recipe", NeoRecipeAddon::new);
    }
}
