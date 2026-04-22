package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.manager.immutable.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonFluidCollection;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonJEI;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class FluidJEI implements NeoAddonJEI {
    private final Map<Fluid, List<FluidStack>> fluidCache = new HashMap<>();
    private boolean cachePopulated = false;
    private @Nullable IJeiRuntime runtime;

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
    public void postCompileAll(AbstractGameStageManager<?> instance, BaseStages stages) {
        iterate(stages, CommonFluidCollection.TYPE, entry -> {
            if (entry instanceof NeoFluidRestrictionEntry.Compiled(
                    var ignored, var gameContent, var predicate, var hideInJEI
            )) {
                if (!hideInJEI) return;
                predicate.addNotifier(newTest -> updateVisibility(newTest, gameContent.fluids(), this::showFluids, this::hideFluids));
            }
        });
    }

    @Override
    public void singleRefreshAll(AbstractGameStageManager<?> instance, BaseStages stages) {
        iterate(stages, CommonFluidCollection.TYPE, entry -> {
            if (entry instanceof NeoFluidRestrictionEntry.Compiled(
                    var ignored, var gameContent, var predicate, var hideInJEI
            )) {
                if (!hideInJEI) return;
                updateVisibility(predicate.test(), gameContent.fluids(), this::showFluids, this::hideFluids);
            }
        });
    }

    public void showFluids(HolderSet<Fluid> fluids) {
        var fluidCache = getFluidCache();
        Objects.requireNonNull(runtime)
                .getIngredientManager()
                .addIngredientsAtRuntime(NeoForgeTypes.FLUID_STACK, fluids.stream()
                        .filter(Objects::nonNull)
                        .map(Holder::value)
                        .map(fluidCache::get)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .toList());
    }

    public void hideFluids(HolderSet<Fluid> fluids) {
        var fluidCache = getFluidCache();
        Objects.requireNonNull(runtime)
                .getIngredientManager()
                .removeIngredientsAtRuntime(NeoForgeTypes.FLUID_STACK, fluids.stream()
                        .filter(Objects::nonNull)
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
        var ingredientManager = Objects.requireNonNull(runtime).getIngredientManager();
        for (var ingredient : ingredientManager.getAllIngredients(NeoForgeTypes.FLUID_STACK)) {
            fluidCache.computeIfAbsent(Objects.requireNonNull(ingredient).getFluid(), unused -> new ArrayList<>(1))
                    .add(ingredient);
        }
        fluidCache.entrySet()
                .forEach(e -> Objects.requireNonNull(e).setValue(Objects.requireNonNull(List.copyOf(e.getValue()))));

        cachePopulated = true;
    }
}
