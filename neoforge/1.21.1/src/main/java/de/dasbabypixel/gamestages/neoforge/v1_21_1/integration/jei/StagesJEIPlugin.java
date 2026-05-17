package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei;

import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonManager;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IIngredientAliasRegistration;
import mezz.jei.api.registration.IModInfoRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.IRuntimeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.config.IJeiConfigManager;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NullMarked;

@JeiPlugin
@NullMarked
public class StagesJEIPlugin implements IModPlugin {
    static {
        NeoAddonManager.registerAddon("jei", () -> JEIAddon.ADDON);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        JEIAddon.REGISTER_CATEGORIES_EVENT.call(new JEIAddon.RegisterCategoriesEvent(registration));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        JEIAddon.REGISTER_RECIPE_CATALYSTS_EVENT.call(new JEIAddon.RegisterRecipeCatalystsEvent(registration));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        JEIAddon.REGISTER_RECIPES_EVENT.call(new JEIAddon.RegisterRecipesEvent(registration));
    }

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
        JEIAddon.REGISTER_ADVANCED_EVENT.call(new JEIAddon.RegisterAdvancedEvent(registration));
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        JEIAddon.RUNTIME_AVAILABLE_EVENT.call(new JEIAddon.RuntimeAvailableEvent(runtime));
    }

    @Override
    public void onRuntimeUnavailable() {
        JEIAddon.RUNTIME_UNAVAILABLE_EVENT.call(new JEIAddon.RuntimeUnavailableEvent());
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
    }

    @Override
    public <T> void registerFluidSubtypes(ISubtypeRegistration registration, IPlatformFluidHelper<T> platformFluidHelper) {
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
    }

    @Override
    public void registerExtraIngredients(IExtraIngredientRegistration registration) {
    }

    @Override
    public void registerIngredientAliases(IIngredientAliasRegistration registration) {
    }

    @Override
    public void registerModInfo(IModInfoRegistration modAliasRegistration) {
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
    }

    @Override
    public void registerRuntime(IRuntimeRegistration registration) {
    }

    @Override
    public void onConfigManagerAvailable(IJeiConfigManager configManager) {
    }

    @Override
    public ResourceLocation getPluginUid() {
        return CommonVGameStageMod.location("game_stages");
    }

    public static void initAddons() {
        JEIIntegration.INIT_JEI_SUPPORT_EVENT.call(new JEIIntegration.InitJEISupportEvent());
    }
}
