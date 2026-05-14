package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.integration.exdeorum;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.exdeorum.IXeiSieveRecipeResult;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thedarkcolour.exdeorum.compat.XeiSieveRecipe;
import thedarkcolour.exdeorum.recipe.RecipeUtil;
import thedarkcolour.exdeorum.recipe.sieve.SieveRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
@Mixin(XeiSieveRecipe.class)
public class EDXeiSieveRecipeMixin {
    @Unique
    private static @Nullable Map<SieveRecipe, RecipeHolder<? extends SieveRecipe>> stages$recipeMap;

    @Inject(method = "getAllRecipesGrouped", at = @At("HEAD"))
    private static void initList(RecipeType<? extends SieveRecipe> recipeType, MutableInt maxRows, CallbackInfoReturnable<ImmutableList<XeiSieveRecipe>> cir) {
        var recipeManager = Objects.requireNonNull(RecipeUtil.getRecipeManager());
        var recipeList = List.copyOf(recipeManager.getAllRecipesFor(recipeType));

        stages$recipeMap = new HashMap<>();
        for (var h : recipeList) {
            if (stages$recipeMap.containsKey(h.value())) {
                throw new IllegalStateException("Duplicate sieve recipe detected?");
            }
            stages$recipeMap.put(h.value(), h);
        }
    }

    @Inject(method = "getAllRecipesGrouped", at = @At("TAIL"))
    private static void cleanupList(RecipeType<? extends SieveRecipe> recipeType, MutableInt maxRows, CallbackInfoReturnable<ImmutableList<XeiSieveRecipe>> cir) {
        stages$recipeMap = null;
    }

    @SuppressWarnings("DataFlowIssue")
    @ModifyArg(method = "getAllRecipesGrouped", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;add(Ljava/lang/Object;)Z"))
    private static <E> E add(E e, @Local(name = "recipe") SieveRecipe recipe, @Local(name = "recipes") List<? extends SieveRecipe> recipes) {
        var result = (XeiSieveRecipe.Result) e;
        ((IXeiSieveRecipeResult) (Object) result).initHolder(Objects.requireNonNull(stages$recipeMap.get(recipe), "Missing recipe"));
        return e;
    }
}
