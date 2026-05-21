package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.jei;

import de.dasbabypixel.gamestages.neoforge.integration.Mod;
import de.dasbabypixel.gamestages.neoforge.integration.Mods;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.ExDeorumJEIIntegration;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei.JEIAddon;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

@NullMarked
public class RecipeJEI {
    private static final Mod EX_DEORUM = Mods.mod("exdeorum");
    private static @Nullable RecipeJEI instance;
    private final RecipeVisibilityUpdater updater = new RecipeVisibilityUpdater(this);
    private @Nullable Context context;

    private RecipeJEI() {
        JEIAddon.RUNTIME_AVAILABLE_EVENT.addListener(this::onRuntimeAvailable);
        JEIAddon.RUNTIME_UNAVAILABLE_EVENT.addListener(this::onRuntimeUnavailable);

        if (EX_DEORUM.isLoaded()) {
            ExDeorumJEIIntegration.init(this);
        }
    }

    public static void init() {
        if (instance != null) throw new IllegalStateException();
        instance = new RecipeJEI();
    }

    public static RecipeManager recipeManager() {
        return Objects.requireNonNull(Minecraft.getInstance().getConnection()).getRecipeManager();
    }

    public void onRuntimeAvailable(JEIAddon.RuntimeAvailableEvent event) {
        var runtime = event.runtime();
        var recipeTypeByJEI = new HashMap<RecipeType<?>, net.minecraft.world.item.crafting.RecipeType<?>>();
        var recipeTypeByMinecraft = new HashMap<net.minecraft.world.item.crafting.RecipeType<?>, RecipeType<?>>();

        for (var category : runtime.getRecipeManager().createRecipeCategoryLookup().includeHidden().get().toList()) {
            Objects.requireNonNull(category);
            var type = category.getRecipeType();
            var minecraftType = BuiltInRegistries.RECIPE_TYPE.get(type.getUid());
            if (minecraftType != null) {
                recipeTypeByJEI.put(type, minecraftType);
                recipeTypeByMinecraft.put(minecraftType, type);
            }
        }

        this.context = new Context(runtime, recipeTypeByJEI, recipeTypeByMinecraft);

        updater.reload(this.context);

        updater.viewerStartup();
    }

    public void onRuntimeUnavailable(JEIAddon.RuntimeUnavailableEvent event) {
        this.context = null;
    }

    public Context context() {
        return Objects.requireNonNull(context);
    }
}
