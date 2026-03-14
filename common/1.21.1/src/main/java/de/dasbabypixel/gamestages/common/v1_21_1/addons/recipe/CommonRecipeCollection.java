package de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.addons.recipe.RecipeCollection;
import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonGameContent;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonGameContentSerializer;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonGameContentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public record CommonRecipeCollection(
        @NonNull List<@NonNull ResourceLocation> recipes) implements RecipeCollection, CommonGameContent {
    public static final @NonNull CommonRecipeCollection EMPTY = new CommonRecipeCollection(List.of());
    public static final @NonNull StreamCodec<RegistryFriendlyByteBuf, CommonRecipeCollection> STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), CommonRecipeCollection::recipes, CommonRecipeCollection::new);
    public static final @NonNull CommonGameContentSerializer<CommonRecipeCollection> SERIALIZER = () -> CommonRecipeCollection.STREAM_CODEC;
    public static RecipeManager recipeManager;
    public static final @NonNull CommonGameContentType<CommonRecipeCollection> TYPE = new CommonGameContentType.AbstractGameContentType<>() {
        @Override
        public @NonNull CommonRecipeCollection modContent(String modId) {
            var recipes = Objects
                    .requireNonNull(recipeManager)
                    .getRecipes()
                    .stream()
                    .map(RecipeHolder::id)
                    .filter(r -> modId.equals(r.getNamespace()))
                    .toList();
            return new CommonRecipeCollection(recipes);
        }
    };

    public CommonRecipeCollection {
        recipes = List.copyOf(recipes);
    }

    @Override
    public @NonNull GameContentType<?> type() {
        return TYPE;
    }

    @Override
    public @NonNull Collection<? extends @NonNull Object> content() {
        return recipes;
    }

    @Override
    public boolean isEmpty() {
        return recipes.isEmpty();
    }

    @Override
    public @NonNull CommonGameContentSerializer<?> serializer() {
        return SERIALIZER;
    }
}
