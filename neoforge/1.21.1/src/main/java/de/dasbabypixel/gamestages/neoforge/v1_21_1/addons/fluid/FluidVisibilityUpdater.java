package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.fluid.FluidType;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei.JEIVisibilityUpdater;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.ingredients.TypedIngredient;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class FluidVisibilityUpdater extends JEIVisibilityUpdater<FluidVisibilityUpdater.JEIContext, FluidVisibilityUpdater.FluidContext, FluidVisibilityUpdater.Entry, FluidStack, NeoFluidRestrictionEntry.Compiled> {
    public FluidVisibilityUpdater() {
        super(FluidType.get());
    }

    @Override
    protected JEIContext createContext(IJeiRuntime runtime) {
        var fluidCache = new HashMap<Fluid, List<FluidStack>>();
        var ingredientManager = runtime.getIngredientManager();
        for (var ingredient : ingredientManager.getAllIngredients(NeoForgeTypes.FLUID_STACK)) {
            assert ingredient != null;
            fluidCache.computeIfAbsent(ingredient.getFluid(), unused -> new ArrayList<>(1)).add(ingredient);
        }
        fluidCache.entrySet().forEach(e -> {
            assert e != null;
            e.setValue(List.copyOf(e.getValue()));
        });

        return new JEIContext(runtime, fluidCache);
    }

    @Override
    protected void initialize(JEIContext jeiContext, FluidContext fluidContext) {

    }

    @Override
    protected void shutdown(JEIContext jeiContext, FluidContext fluidContext) {

    }

    @Override
    protected void collect(JEIContext jeiContext, FluidContext fluidContext, BaseStages stages, BaseStages.CompileIndex compileIndex, NeoFluidRestrictionEntry.Compiled compiledEntry, Collector collector) {
        var fluidSet = compiledEntry.gameContent().gameContent().elements();
        var fluids = getFluids(jeiContext, fluidSet);
        if (fluids.isEmpty()) return;
        for (var fluid : fluids) {
            collector.add(compiledEntry.predicate(), fluid);
        }
    }

    private List<FluidStack> getFluids(JEIContext context, HolderSet<Fluid> fluidSet) {
        var fluidCache = context.fluidCache();
        return fluidSet
                .stream()
                .map(Objects::requireNonNull)
                .map(Holder::value)
                .map(fluidCache::get)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .toList();
    }

    @Override
    protected boolean shouldBeVisible(Entry entry) {
        return entry.predicate().test();
    }

    @Override
    protected void show(List<FluidStack> show) {
        context().runtime().getIngredientManager().addIngredientsAtRuntime(NeoForgeTypes.FLUID_STACK, show);
    }

    @Override
    protected void hide(List<FluidStack> hide) {
        removeBookmarks(hide);
        context().runtime().getIngredientManager().removeIngredientsAtRuntime(NeoForgeTypes.FLUID_STACK, hide);
    }

    private void removeBookmarks(List<FluidStack> fluids) {
        var ingredientManager = context().runtime().getIngredientManager();
        for (var fluidStack : fluids) {
            var ingredient = TypedIngredient.createAndFilterInvalid(ingredientManager, NeoForgeTypes.FLUID_STACK, fluidStack, false);
            if (ingredient == null) continue;
            context().runtime().getBookmarkManager().remove(ingredient);
        }
    }

    @Override
    protected FluidStack extract(Entry entry) {
        return entry.stack();
    }

    @Override
    protected Entry createWrapper(FluidStack fluidStack, List<CompiledRestrictionPredicate> relevantEntries) {
        if (relevantEntries.size() != 1) throw new IllegalStateException();
        var predicate = relevantEntries.getFirst();
        return new Entry(fluidStack, predicate);
    }

    public record Entry(FluidStack stack, CompiledRestrictionPredicate predicate) {
    }

    public record JEIContext(IJeiRuntime runtime, Map<Fluid, List<FluidStack>> fluidCache) {
        public JEIContext {
            fluidCache = Map.copyOf(fluidCache);
        }
    }

    public record FluidContext() {
    }
}
