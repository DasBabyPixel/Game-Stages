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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@NullMarked
public record CommonRecipeCollection(List<ResourceLocation> recipes) implements RecipeCollection, CommonGameContent {
    public static final CommonRecipeCollection EMPTY = new CommonRecipeCollection(List.of());
    @SuppressWarnings("DataFlowIssue")
    public static final StreamCodec<RegistryFriendlyByteBuf, CommonRecipeCollection> STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), CommonRecipeCollection::recipes, CommonRecipeCollection::new);
    public static final CommonGameContentSerializer<CommonRecipeCollection> SERIALIZER = () -> CommonRecipeCollection.STREAM_CODEC;
    public static @Nullable RecipeManager recipeManager;
    public static final CommonGameContentType<CommonRecipeCollection> TYPE = new CommonGameContentType.AbstractGameContentType<>() {
        @Override
        public CommonRecipeCollection modContent(String modId) {
            var recipes = Objects.requireNonNull(recipeManager, "RecipeManager is null")
                    .getRecipes()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(RecipeHolder::id)
                    .filter(r -> modId.equals(r.getNamespace()))
                    .toList();
            return new CommonRecipeCollection(recipes);
        }
    };

    public CommonRecipeCollection {
        recipes = Objects.requireNonNull(List.copyOf(recipes));
    }

    @Override
    public GameContentType<?> type() {
        return TYPE;
    }

    @Override
    public Collection<? extends Object> content() {
        return recipes;
    }

    @Override
    public Collection<? extends Object> contentCollection() {
        return recipes;
    }

    @Override
    public boolean isEmpty() {
        return recipes.isEmpty();
    }

    @Override
    public CommonGameContentSerializer<?> serializer() {
        return SERIALIZER;
    }
}
