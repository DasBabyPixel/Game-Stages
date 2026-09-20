package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe.integration.polymorph;

import com.illusivesoulworks.polymorph.api.common.capability.IRecipeData;
import com.illusivesoulworks.polymorph.common.PolymorphRecipeManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.RecipeThreadLocal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;

@NullMarked
@Mixin(value = PolymorphRecipeManager.class, remap = false)
public class PolymorphRecipeManagerMixin {
    @WrapOperation(method = "getBlockEntityRecipe", at = @At(value = "INVOKE", target = "Lcom/illusivesoulworks/polymorph/common/PolymorphRecipeManager;getRecipe(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;Lcom/illusivesoulworks/polymorph/api/common/capability/IRecipeData;Ljava/util/List;)Ljava/util/Optional;"))
    public <T extends Recipe<I>, I extends RecipeInput> Optional<RecipeHolder<T>> stages$wrapBlockEntity(RecipeType<T> type, I inventory, Level level, IRecipeData<?> recipeData, List<RecipeHolder<T>> recipes, Operation<Optional<RecipeHolder<T>>> original, @Local(argsOnly = true) BlockEntity blockEntity) {
        var stages = level.isClientSide ? null : blockEntity.stages();
        var l = RecipeThreadLocal.get();
        l.pushStages(stages);
        try {
            return original.call(type, inventory, level, recipeData, recipes);
        } finally {
            l.popStages();
        }
    }

    @WrapOperation(method = "getPlayerRecipe", at = @At(value = "INVOKE", target = "Lcom/illusivesoulworks/polymorph/common/PolymorphRecipeManager;getRecipe(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;Lcom/illusivesoulworks/polymorph/api/common/capability/IRecipeData;Ljava/util/List;)Ljava/util/Optional;"))
    public <T extends Recipe<I>, I extends RecipeInput> Optional<RecipeHolder<T>> stages$wrapPlayer(RecipeType<T> type, I inventory, Level level, IRecipeData<?> recipeData, List<RecipeHolder<T>> recipes, Operation<Optional<RecipeHolder<T>>> original, @Local(argsOnly = true) Player player) {
        var stages = player.getGameStages();
        var l = RecipeThreadLocal.get();
        l.pushStages(stages);
        try {
            return original.call(type, inventory, level, recipeData, recipes);
        } finally {
            l.popStages();
        }
    }
}
