package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import de.dasbabypixel.gamestages.common.data.manager.immutable.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.VRecipeAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.IGhostRecipe;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@NullMarked
@Mixin(GhostRecipe.class)
@Implements(@Interface(iface = IGhostRecipe.class, prefix = "stages$"))
public abstract class MCGhostRecipeMixin implements IGhostRecipe {
    @Shadow
    @Nullable
    private RecipeHolder<?> recipe;

    @Shadow
    @Nullable
    public abstract RecipeHolder<?> getRecipe();

    @Shadow
    public abstract void clear();

    public void stages$recipesUpdated() {
        if (recipe != null) {
            var entry = VRecipeAddon.getEntry(ClientGameStageManager.stages(), recipe);
            if (entry != null && !entry.predicate().test()) {
                this.clear();
            }
        }
    }
}
