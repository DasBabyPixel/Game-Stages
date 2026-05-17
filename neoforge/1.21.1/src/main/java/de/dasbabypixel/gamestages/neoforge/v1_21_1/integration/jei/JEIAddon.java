package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei;

import de.dasbabypixel.gamestages.common.event.EventType;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class JEIAddon implements NeoAddon {
    public static final JEIAddon ADDON = new JEIAddon();
    public static final EventType<RegisterCategoriesEvent> REGISTER_CATEGORIES_EVENT = EventType.create();
    public static final EventType<RegisterRecipeCatalystsEvent> REGISTER_RECIPE_CATALYSTS_EVENT = EventType.create();
    public static final EventType<RegisterRecipesEvent> REGISTER_RECIPES_EVENT = EventType.create();
    public static final EventType<RegisterAdvancedEvent> REGISTER_ADVANCED_EVENT = EventType.create();
    public static final EventType<RuntimeAvailableEvent> RUNTIME_AVAILABLE_EVENT = EventType.create();
    public static final EventType<RuntimeUnavailableEvent> RUNTIME_UNAVAILABLE_EVENT = EventType.create();

    private JEIAddon() {
        FINISH_STARTUP_EVENT.addListener(this::handle);
    }

    private void handle(FinishStartupEvent event) {
        StagesJEIPlugin.initAddons();
    }

    public record RegisterAdvancedEvent(IAdvancedRegistration registration) {
    }

    public record RegisterCategoriesEvent(IRecipeCategoryRegistration registration) {
    }

    public record RegisterRecipeCatalystsEvent(IRecipeCatalystRegistration registration) {
    }

    public record RegisterRecipesEvent(IRecipeRegistration registration) {
    }

    public record RuntimeAvailableEvent(IJeiRuntime runtime) {
    }

    public record RuntimeUnavailableEvent() {
    }
}
