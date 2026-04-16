package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeCollection;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonJEI;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class RecipeJEI implements NeoAddonJEI {
    private @Nullable IJeiRuntime runtime;

    @Override
    public void singleRefreshAll(AbstractGameStageManager<?> instance, BaseStages stages) {
        iterate(stages, CommonRecipeCollection.TYPE, entry -> {
            if (entry instanceof NeoRecipeRestrictionEntry.Compiled compiled) {
                if (!compiled.hideInJEI()) return;
                System.out.println(compiled.gameContent().contentCollection().size() + ": " + compiled.predicate()
                        .test());
            }
        });
    }

    @Override
    public void postCompileAll(AbstractGameStageManager<?> instance, BaseStages stages) {
        iterate(stages, CommonRecipeCollection.TYPE, entry -> {
            if (entry instanceof NeoRecipeRestrictionEntry.Compiled compiled) {
                if (!compiled.hideInJEI()) return;
                compiled.predicate()
                        .addNotifier(newTest -> System.out.println(compiled.gameContent()
                                .contentCollection()
                                .size() + ": " + newTest));
            }
        });

//        var player = stages.getPlayer();
//        var registries = ((Player) player).registryAccess();
        Objects.requireNonNull(runtime)
                .getRecipeManager()
                .createRecipeLookup(RecipeTypes.CRAFTING)
                .includeHidden()
                .get()
                .forEach(holder -> {
                    var recipe = holder.value();
//            System.out.println(recipe.getResultItem(registries));
                    for (var ingredient : recipe.getIngredients()) {
//                System.out.println(" - " + Arrays
//                        .stream(ingredient.getItems())
//                        .map(ItemStack::getItem)
//                        .map(BuiltInRegistries.ITEM::getKey)
//                        .map(ResourceLocation::toString)
//                        .toList());
                    }
                });
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onRuntimeUnavailable() {
        this.runtime = null;
    }
}
