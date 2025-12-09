package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.CommonVGameStageMod;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NonNull;

import java.util.*;

@JeiPlugin
public class StagesJEIPlugin implements IModPlugin {
    private static final Map<Item, List<ItemStack>> ITEM_CACHE = new HashMap<>();
    private static final Map<Fluid, List<FluidStack>> FLUID_CACHE = new HashMap<>();
    private static boolean cachePopulated = false;
    private static IJeiRuntime runtime;

    static {
        ClientGameStageManager.ADDONS.add(JEIAddon.ADDON);
    }

    public static void showItems(HolderSet<Item> items) {
        var itemCache = getItemCache();
        runtime
                .getIngredientManager()
                .addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, items
                        .stream()
                        .map(Holder::value)
                        .map(itemCache::get)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .toList());
    }

    public static void hideItems(HolderSet<Item> items) {
        var itemCache = getItemCache();
        runtime
                .getIngredientManager()
                .removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, items
                        .stream()
                        .map(Holder::value)
                        .map(itemCache::get)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .toList());
    }

    public static void showFluids(HolderSet<Fluid> items) {
        var fluidCache = getFluidCache();
        runtime
                .getIngredientManager()
                .addIngredientsAtRuntime(NeoForgeTypes.FLUID_STACK, items
                        .stream()
                        .map(Holder::value)
                        .map(fluidCache::get)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .toList());
    }

    public static void hideFluids(HolderSet<Fluid> items) {
        var fluidCache = getFluidCache();
        runtime
                .getIngredientManager()
                .removeIngredientsAtRuntime(NeoForgeTypes.FLUID_STACK, items
                        .stream()
                        .map(Holder::value)
                        .map(fluidCache::get)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .toList());
    }

    private static void clearCache() {
        ITEM_CACHE.clear();
        FLUID_CACHE.clear();
        cachePopulated = false;
    }

    private static Map<Item, List<ItemStack>> getItemCache() {
        if (!cachePopulated) populateCache();
        return ITEM_CACHE;
    }

    private static Map<Fluid, List<FluidStack>> getFluidCache() {
        if (!cachePopulated) populateCache();
        return FLUID_CACHE;
    }

    private static void populateCache() {
        var ingredientManager = runtime.getIngredientManager();
        for (var ingredient : ingredientManager.getAllIngredients(VanillaTypes.ITEM_STACK)) {
            ITEM_CACHE.computeIfAbsent(ingredient.getItem(), unused -> new ArrayList<>(1)).add(ingredient);
        }
        ITEM_CACHE.entrySet().forEach(e -> e.setValue(List.copyOf(e.getValue())));
        for (var ingredient : ingredientManager.getAllIngredients(NeoForgeTypes.FLUID_STACK)) {
            FLUID_CACHE.computeIfAbsent(ingredient.getFluid(), unused -> new ArrayList<>(1)).add(ingredient);
        }
        FLUID_CACHE.entrySet().forEach(e -> e.setValue(List.copyOf(e.getValue())));

        cachePopulated = true;
    }

    @Override
    public void onRuntimeAvailable(@NonNull IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
        var player = CommonInstances.platformPlayerProvider.clientSelfPlayer();
        if (player != null) {
            JEIAddon.ADDON.singleRefreshAll(player.getGameStages());
        }
    }

    @Override
    public void onRuntimeUnavailable() {
        clearCache();
        runtime = null;
    }

    @Override
    public @NonNull ResourceLocation getPluginUid() {
        return CommonVGameStageMod.location("game_stages");
    }
}
