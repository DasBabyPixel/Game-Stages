package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.RecipeType;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.jsapi.GameCollectionRecipeJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.server.ServerRegisterEventJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.probejs.StagesProbeJSPlugin;
import moe.wolfgirl.probejs.plugin.builtins.alias.SpecialTypes;
import moe.wolfgirl.probejs.typescript.base.AliasRegistrar;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

import static de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.probejs.StagesProbeJSPlugin.transformerRegistry;
import static moe.wolfgirl.probejs.typescript.document.Types.clazz;
import static moe.wolfgirl.probejs.typescript.document.Types.union;
import static moe.wolfgirl.probejs.typescript.document.Types.wrapped;

@NullMarked
public class RecipeProbeJS implements NeoAddonProbeJS {
    static {
        registerTransformers();
    }

    @SuppressWarnings({"DataFlowIssue", "CodeBlock2Expr"})
    private static void registerTransformers() {
        var recipeCollection = StagesProbeJSPlugin.typedCollection(RecipeType.get());
        var usingOnlyRecipeCollection = StagesProbeJSPlugin.collectionUsingOnly(RecipeType.get());

        transformerRegistry.register(ServerRegisterEventJS.class, (classDecl, methodDecl) -> {
            methodDecl.returnType = recipeCollection;
            methodDecl.params.getFirst().typeInfo = usingOnlyRecipeCollection;
        }, "recipes");
        transformerRegistry.register(ServerRegisterEventJS.class, (classDecl, methodDecl) -> {
            methodDecl.params.get(1).typeInfo = usingOnlyRecipeCollection;
        }, "restrictRecipes");
    }

    @Override
    public void addTypeAlias(AliasRegistrar registrar) {
        {
            var recipeId = SpecialTypes.RECIPE_ID;
            var recipeIdExplicit = wrapped("`.${%s}`", SpecialTypes.RECIPE_ID);
            var mod = wrapped("`@${%s}`", SpecialTypes.MOD_ID);
            var recursive = Objects.requireNonNull(clazz(GameCollectionRecipeJS.class).asInput()).asArray();
            var recipe = union(recipeId, recipeIdExplicit, mod, recursive);
            registrar.addInputAlias(GameCollectionRecipeJS.class, recipe.markAsInput());
        }
    }
}
