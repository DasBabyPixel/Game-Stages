package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.PlayerStages;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeCollection;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonJEI;
import mezz.jei.api.runtime.IJeiRuntime;
import org.jspecify.annotations.NonNull;

public class RecipeJEI implements NeoAddonJEI {
    private IJeiRuntime runtime;

    @Override
    public void singleRefreshAll(@NonNull AbstractGameStageManager instance, @NonNull PlayerStages stages) {
        iterate(stages, CommonRecipeCollection.TYPE, entry -> {
            if (entry instanceof NeoRecipeRestrictionEntry.Compiled(var e, var gameContent, var predicate)) {
                if (!e.hideInJEI()) return;
                System.out.println(gameContent.content().size() + ": " + predicate.test());
            }
        });
    }

    @Override
    public void postCompileAll(@NonNull AbstractGameStageManager instance, @NonNull PlayerStages stages) {
        iterate(stages, CommonRecipeCollection.TYPE, entry -> {
            if (entry instanceof NeoRecipeRestrictionEntry.Compiled(var e, var gameContent, var predicate)) {
                if (!e.hideInJEI()) return;
                predicate.addNotifier(newTest -> System.out.println(gameContent.content().size() + ": " + newTest));
            }
        });
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        this.runtime = runtime;
//        var stream = runtime.getRecipeManager().createRecipeLookup(RecipeTypes.CRAFTING).includeHidden().get();
    }

    @Override
    public void onRuntimeUnavailable() {
        this.runtime = null;
    }
}
