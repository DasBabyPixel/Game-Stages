package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeCollection;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonJEI;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import org.jspecify.annotations.NonNull;

public class RecipeJEI implements NeoAddonJEI {
    private IJeiRuntime runtime;

    @Override
    public void singleRefreshAll(@NonNull AbstractGameStageManager instance, @NonNull BaseStages stages) {
        iterate(stages, CommonRecipeCollection.TYPE, entry -> {
            if (entry instanceof NeoRecipeRestrictionEntry.Compiled(var e, var gameContent, var predicate)) {
                if (!e.hideInJEI()) return;
                System.out.println(gameContent.content().size() + ": " + predicate.test());
            }
        });
    }

    @Override
    public void postCompileAll(@NonNull AbstractGameStageManager instance, @NonNull BaseStages stages) {
        iterate(stages, CommonRecipeCollection.TYPE, entry -> {
            if (entry instanceof NeoRecipeRestrictionEntry.Compiled(var e, var gameContent, var predicate)) {
                if (!e.hideInJEI()) return;
                predicate.addNotifier(newTest -> System.out.println(gameContent.content().size() + ": " + newTest));
            }
        });

//        var player = stages.getPlayer();
//        var registries = ((Player) player).registryAccess();
        runtime.getRecipeManager().createRecipeLookup(RecipeTypes.CRAFTING).includeHidden().get().forEach(holder -> {
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
