package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import thedarkcolour.exdeorum.recipe.sieve.SieveRecipe;

import java.util.Objects;

@NullMarked
public class JEIUpdatableRecipe<T extends SieveRecipe> {
    private final BuildableSieveRecipe<T> buildable;
    private @Nullable JEISieveRecipe<T> recipe;
    private boolean isVisible;

    public JEIUpdatableRecipe(BuildableSieveRecipe<T> buildable) {
        this.buildable = buildable;
    }

    public @Nullable ResourceLocation identifier() {
        return buildable.identifier();
    }

    public BuildableSieveRecipe<T> buildable() {
        return buildable;
    }

    public void clearCache() {
        recipe = null;
    }

    public boolean isVisible() {
        ensureCached();
        return isVisible;
    }

    public JEISieveRecipe<T> recipe() {
        ensureCached();
        return Objects.requireNonNull(recipe);
    }

    private void ensureCached() {
        if (recipe != null) return;
        loadCache();
    }

    private void loadCache() {
        var stages = Objects.requireNonNull(Minecraft.getInstance().player).getGameStages();
        var builder = buildable.build(stages);
        isVisible = builder.canBuild();
        recipe = builder.build();
    }
}
