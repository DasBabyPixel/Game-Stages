package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.BuildConstants;
import net.neoforged.fml.common.Mod;
import org.jspecify.annotations.NullMarked;

import static de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonManager.registerAddon;

@Mod(BuildConstants.MOD_ID)
@NullMarked
public class RecipeMod {
    public RecipeMod() {
        registerAddon("recipe", NeoRecipeAddon::new);
    }
}
