package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.integration.exdeorum;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.exdeorum.IXeiSieveRecipeResult;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import thedarkcolour.exdeorum.compat.XeiSieveRecipe;
import thedarkcolour.exdeorum.recipe.sieve.SieveRecipe;

import java.util.Objects;

@NullMarked
@Mixin(XeiSieveRecipe.Result.class)
@Implements(@Interface(iface = IXeiSieveRecipeResult.class, prefix = "stages$"))
public class EDXeiSieveRecipeResultMixin {
    @Unique
    private @Nullable RecipeHolder<? extends SieveRecipe> stages$holder;

    public void stages$initHolder(RecipeHolder<? extends SieveRecipe> holder) {
        this.stages$holder = Objects.requireNonNull(holder);
    }

    public RecipeHolder<? extends SieveRecipe> stages$holder() {
        return Objects.requireNonNull(stages$holder);
    }
}
