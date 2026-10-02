package de.dasbabypixel.gamestages.neoforge.v1_21_1.mixins.recipe;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.IOverlayRecipeComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@NullMarked
@Mixin(OverlayRecipeComponent.class)
@Implements(@Interface(iface = IOverlayRecipeComponent.class, prefix = "stages$"))
public abstract class MCOverlayRecipeComponentMixin implements IOverlayRecipeComponent {
    @Shadow
    @Nullable
    private RecipeHolder<?> lastRecipeClicked;
    @Shadow
    private Minecraft minecraft;
    @Shadow
    @Nullable
    private RecipeCollection collection;
    @Shadow
    @Final
    private List<?> recipeButtons;
    @Unique
    private int stages$lastX;
    @Unique
    private int stages$lastY;
    @Unique
    private int stages$lastArg1;
    @Unique
    private int stages$lastArg2;
    @Unique
    private float stages$lastArg3;

    @Shadow
    public abstract boolean isVisible();

    @Shadow
    public abstract void init(Minecraft minecraft, RecipeCollection collection, int x, int y, int p_100199_, int p_100200_, float p_100201_);

    @Inject(method = "init", at = @At("HEAD"))
    private void init(Minecraft minecraft, RecipeCollection collection, int x, int y, int p_100199_, int p_100200_, float p_100201_, CallbackInfo ci) {
        this.stages$lastX = x;
        this.stages$lastY = y;
        this.stages$lastArg1 = p_100199_;
        this.stages$lastArg2 = p_100200_;
        this.stages$lastArg3 = p_100201_;
    }

    public void stages$recipesUpdated() {
        if (isVisible()) {
            this.init(minecraft, Objects.requireNonNull(collection), stages$lastX, stages$lastY, stages$lastArg1, stages$lastArg2, stages$lastArg3);
        } else {
            this.recipeButtons.clear();
            this.collection = null;
        }
        lastRecipeClicked = null;
    }
}
