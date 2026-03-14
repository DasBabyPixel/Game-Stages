package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonJEI;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonManager;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@JeiPlugin
public class StagesJEIPlugin implements IModPlugin {
    private static final Map<NeoAddon, NeoAddonJEI> ADDON_MAP = new HashMap<>();
    private static boolean populated = false;

//    private static final Map<Fluid, List<FluidStack>> FLUID_CACHE = new HashMap<>();

    static {
        NeoAddonManager.registerAddon(() -> JEIAddon.ADDON);
    }

//    public static void showFluids(HolderSet<Fluid> items) {
//        var fluidCache = getFluidCache();
//        runtime
//                .getIngredientManager()
//                .addIngredientsAtRuntime(NeoForgeTypes.FLUID_STACK, items
//                        .stream()
//                        .map(Holder::value)
//                        .map(fluidCache::get)
//                        .filter(Objects::nonNull)
//                        .flatMap(Collection::stream)
//                        .toList());
//    }
//
//    public static void hideFluids(HolderSet<Fluid> items) {
//        var fluidCache = getFluidCache();
//        runtime
//                .getIngredientManager()
//                .removeIngredientsAtRuntime(NeoForgeTypes.FLUID_STACK, items
//                        .stream()
//                        .map(Holder::value)
//                        .map(fluidCache::get)
//                        .filter(Objects::nonNull)
//                        .flatMap(Collection::stream)
//                        .toList());
//    }
//
//    private static void clearCache() {
//        FLUID_CACHE.clear();
//    }
//
//    private static Map<Fluid, List<FluidStack>> getFluidCache() {
//        if (!cachePopulated) populateCache();
//        return FLUID_CACHE;
//    }
//
//    private static void populateCache() {
//        var ingredientManager = runtime.getIngredientManager();
//        for (var ingredient : ingredientManager.getAllIngredients(NeoForgeTypes.FLUID_STACK)) {
//            FLUID_CACHE.computeIfAbsent(ingredient.getFluid(), unused -> new ArrayList<>(1)).add(ingredient);
//        }
//        FLUID_CACHE.entrySet().forEach(e -> e.setValue(List.copyOf(e.getValue())));
//
//        var stream = runtime.getRecipeManager().createRecipeLookup(RecipeTypes.CRAFTING).includeHidden().get();
//
//
//        cachePopulated = true;
//    }


    public static Map<NeoAddon, NeoAddonJEI> addonMap() {
        if (!populated) {
            for (var addon : NeoAddonManager.instance().addons()) {
                ADDON_MAP.put(addon, addon.createJEISupport());
            }
            populated = true;
        }
        return ADDON_MAP;
    }

    public static Collection<NeoAddonJEI> addons() {
        return addonMap().values();
    }

    @Override
    public void onRuntimeAvailable(@NonNull IJeiRuntime jeiRuntime) {
        for (var addon : addonMap().values()) {
            addon.onRuntimeAvailable(jeiRuntime);
        }
        var player = CommonInstances.platformPlayerProvider.clientSelfPlayer();
        if (player != null) {
            JEIAddon.ADDON.singleRefreshAll(ClientGameStageManager.instance(), player.getGameStages());
        }
    }

    @Override
    public void onRuntimeUnavailable() {
        for (var addon : addonMap().values()) {
            addon.onRuntimeUnavailable();
        }
    }

    @Override
    public @NonNull ResourceLocation getPluginUid() {
        return CommonVGameStageMod.location("game_stages");
    }
}
