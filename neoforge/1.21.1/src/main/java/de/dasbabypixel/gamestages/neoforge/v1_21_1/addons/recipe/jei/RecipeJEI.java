package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.jei;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.addon.ClientEvents;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ClientGameStageManager;
import de.dasbabypixel.gamestages.neoforge.integration.Mod;
import de.dasbabypixel.gamestages.neoforge.integration.Mods;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.ExDeorumJEIIntegration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class RecipeJEI {
    private static final Mod EX_DEORUM = Mods.mod("exdeorum");
    private static @Nullable RecipeJEI instance;
    private final RecipeVisibilityUpdater updater = new RecipeVisibilityUpdater();

    private RecipeJEI() {
        if (EX_DEORUM.isLoaded()) {
            ExDeorumJEIIntegration.init(this);
        }

        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, RecipesUpdatedEvent.class, event -> {
            if (!ClientGameStageManager.initialized()) throw new IllegalStateException("Stages not received");
            updater.markReady(new RecipeVisibilityUpdater.RecipeContext(event.getRecipeManager()));
        });
        ClientEvents.CLIENT_DISABLE.addListener(event -> {
            updater.markUnready();
        });

        Addon.CLIENT_POST_SYNC_UNLOCKED_STAGES_EVENT.addListener(event -> {
            var player = Minecraft.getInstance().player;
            if (player == null) return;
            var recipeBook = player.getRecipeBook();
            // Might not be performant but works for now. May need to only update changed recipes in the future
            for (var collection : recipeBook.getCollections()) {
                Objects.requireNonNull(collection);
                collection.updateValidRecipes();
            }

            if (Minecraft.getInstance().screen instanceof RecipeUpdateListener listener) {
                listener.recipesUpdated();
            }
        });
    }

    public static void init() {
        if (instance != null) throw new IllegalStateException();
        instance = new RecipeJEI();
    }
}
