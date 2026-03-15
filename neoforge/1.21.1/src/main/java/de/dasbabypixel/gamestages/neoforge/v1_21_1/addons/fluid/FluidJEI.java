package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.PlayerStages;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonFluidCollection;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonJEI;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class FluidJEI implements NeoAddonJEI {
    private final Map<Fluid, List<FluidStack>> fluidCache = new HashMap<>();
    private boolean cachePopulated = false;
    private IJeiRuntime runtime;

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onRuntimeUnavailable() {
        clearCache();
        this.runtime = null;
    }

    @Override
    public void postCompileAll(@NonNull AbstractGameStageManager instance, @NonNull PlayerStages stages) {
        iterate(stages, CommonFluidCollection.TYPE, entry -> {
            if (entry instanceof NeoFluidRestrictionEntry.Compiled(var e, var gameContent, var predicate)) {
                if (!e.hideInJEI()) return;
                predicate.addNotifier(newTest -> updateVisibility(newTest, gameContent.fluids(), this::showFluids, this::hideFluids));
            }
        });
    }

    @Override
    public void singleRefreshAll(@NonNull AbstractGameStageManager instance, @NonNull PlayerStages stages) {
        iterate(stages, CommonFluidCollection.TYPE, entry -> {
            if (entry instanceof NeoFluidRestrictionEntry.Compiled(var e, var gameContent, var predicate)) {
                if (!e.hideInJEI()) return;
                updateVisibility(predicate.test(), gameContent.fluids(), this::showFluids, this::hideFluids);
            }
        });
    }

    public void showFluids(HolderSet<Fluid> fluids) {
        var fluidCache = getFluidCache();
        runtime
                .getIngredientManager()
                .addIngredientsAtRuntime(NeoForgeTypes.FLUID_STACK, fluids
                        .stream()
                        .map(Holder::value)
                        .map(fluidCache::get)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .toList());
    }

    public void hideFluids(HolderSet<Fluid> fluids) {
        var fluidCache = getFluidCache();
        runtime
                .getIngredientManager()
                .removeIngredientsAtRuntime(NeoForgeTypes.FLUID_STACK, fluids
                        .stream()
                        .map(Holder::value)
                        .map(fluidCache::get)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .toList());
    }

    private void clearCache() {
        fluidCache.clear();
        cachePopulated = false;
    }

    private Map<Fluid, List<FluidStack>> getFluidCache() {
        if (!cachePopulated) populateCache();
        return fluidCache;
    }

    private void populateCache() {
        var ingredientManager = runtime.getIngredientManager();
        for (var ingredient : ingredientManager.getAllIngredients(NeoForgeTypes.FLUID_STACK)) {
            fluidCache.computeIfAbsent(ingredient.getFluid(), unused -> new ArrayList<>(1)).add(ingredient);
        }
        fluidCache.entrySet().forEach(e -> e.setValue(List.copyOf(e.getValue())));

        cachePopulated = true;
    }
}
