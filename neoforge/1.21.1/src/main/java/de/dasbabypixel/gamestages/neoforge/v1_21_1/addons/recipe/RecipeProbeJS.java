package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;
import moe.wolfgirl.probejs.plugin.builtins.alias.RecordTypes;
import moe.wolfgirl.probejs.plugin.builtins.alias.SpecialTypes;
import moe.wolfgirl.probejs.typescript.base.AliasRegistrar;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

import static moe.wolfgirl.probejs.typescript.document.Types.clazz;
import static moe.wolfgirl.probejs.typescript.document.Types.union;
import static moe.wolfgirl.probejs.typescript.document.Types.wrapped;

@NullMarked
public class RecipeProbeJS implements NeoAddonProbeJS {
    {
        RecordTypes.SKIP_RECORDS.add(RecipeCollectionWrapper.class);
    }

    @Override
    public void addTypeAlias(AliasRegistrar registrar) {
        {
            var recipeId = SpecialTypes.RECIPE_ID;
            var recipeIdExplicit = wrapped("`.${%s}`", SpecialTypes.RECIPE_ID);
            var mod = wrapped("`@${%s}`", SpecialTypes.MOD_ID);
            var recursive = Objects.requireNonNull(clazz(RecipeCollectionWrapper.class).asInput()).asArray();
            var recipe = union(recipeId, recipeIdExplicit, mod, recursive);
            registrar.addInputAlias(RecipeCollectionWrapper.class, recipe.markAsInput());
        }
    }
}
