package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.event.EventType;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class JEIAddon implements NeoAddon {
    public static final JEIAddon ADDON = new JEIAddon();
    public static final EventType<RegisterCategoriesEvent> REGISTER_CATEGORIES_EVENT = EventType.create();
    public static final EventType<RegisterRecipeCatalystsEvent> REGISTER_RECIPE_CATALYSTS_EVENT = EventType.create();
    public static final EventType<RegisterRecipesEvent> REGISTER_RECIPES_EVENT = EventType.create();
    public static final EventType<RegisterAdvancedEvent> REGISTER_ADVANCED_EVENT = EventType.create();

    private JEIAddon() {
        CLIENT_RECOMPILE_PRE_EVENT.addListener(this::handle);
        CLIENT_RECOMPILE_POST_EVENT.addListener(this::handle);
        FINISH_STARTUP_EVENT.addListener(this::handle);
    }

    private void handle(FinishStartupEvent event) {
        StagesJEIPlugin.addons(); // Init addons
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

    public record RegisterAdvancedEvent(IAdvancedRegistration registration) {
    }

    public record RegisterCategoriesEvent(IRecipeCategoryRegistration registration) {
    }

    public record RegisterRecipeCatalystsEvent(IRecipeCatalystRegistration registration) {
    }

    public record RegisterRecipesEvent(IRecipeRegistration registration) {
    }
}
