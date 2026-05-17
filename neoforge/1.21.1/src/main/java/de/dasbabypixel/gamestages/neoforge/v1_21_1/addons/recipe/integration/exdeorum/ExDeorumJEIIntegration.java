package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve.SieveCompat;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.config.GameStagesClientConfig;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei.JEIAddon;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ExDeorumJEIIntegration {
    private static boolean override = false;

    public static boolean disabled() {
        return !override;
    }

    public static void init() {
        SieveCompat.init();

        JEIAddon.REGISTER_CATEGORIES_EVENT.addListener(/* call very early */-100, ExDeorumJEIIntegration::registerCategories);
    }

    private static void registerCategories(JEIAddon.RegisterCategoriesEvent event) {
        override = GameStagesClientConfig.CONFIG.exdeorumOverrideJEI.isTrue();
    }
}
