package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ClientGameStageManager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class SieveRecipeProvider implements Buildable<JEISieveRecipe> {
    private final BuildableSieveRecipe buildable;

    public SieveRecipeProvider(BuildableSieveRecipe buildable) {
        this.buildable = buildable;
    }

    public BuildableSieveRecipe get() {
        return buildable;
    }

    public @Nullable JEISieveRecipe gen() {
        var stages = ClientGameStageManager.stages();
        var builder = buildable.build(stages);
        return builder.canBuild() ? builder.build() : null;
    }

    @Override
    public BuildableSieveRecipe.ABuilder build(BaseStages stages) {
        return buildable.build(stages);
    }
}
