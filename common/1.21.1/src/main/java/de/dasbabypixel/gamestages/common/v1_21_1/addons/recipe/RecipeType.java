package de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.CommonInstances;
import de.dasbabypixel.gamestages.common.data.GameContentRegistry;
import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.v1_21_1.data.GameContentSerializers;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@NullMarked
public class RecipeType implements GameContentType<RecipeType.RecipeData, List<ResourceLocation>, ResourceLocation> {
    private static GameContentRegistry.@Nullable Entry<?, RecipeType.RecipeData, List<ResourceLocation>, ResourceLocation> TYPE;
    public @Nullable RecipeManager recipeManager;

    @SuppressWarnings("unchecked")
    public static GameContentRegistry.Entry<?, RecipeData, List<ResourceLocation>, ResourceLocation> get() {
        if (TYPE != null) return TYPE;
        return TYPE = (GameContentRegistry.Entry<?, @NonNull RecipeData, @NonNull List<ResourceLocation>, @NonNull ResourceLocation>) CommonInstances.gameContentRegistry.byTypeId("recipe");
    }

    public static void register(GameContentRegistry.Builder builder) {
        GameContentRegistry.Builder.Entry<? extends GameContentRegistry.Builder.Entry<?, ? extends GameContentRegistry.Entry<?, RecipeData, List<ResourceLocation>, ResourceLocation>, RecipeData, List<ResourceLocation>, ResourceLocation>, ? extends GameContentRegistry.Entry<?, RecipeData, List<ResourceLocation>, ResourceLocation>, RecipeData, List<ResourceLocation>, ResourceLocation> entry = builder.register("recipe", new RecipeType());
        entry.init(GameContentSerializers.attributeStreamCodecElements(), ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()));
    }

    @Override
    public RecipeData newTypeData(TypedGameContent<RecipeData, List<ResourceLocation>, ResourceLocation> content) {
        return new RecipeData();
    }

    @Override
    public List<ResourceLocation> modContent(String modId) {
        return Objects
                .requireNonNull(recipeManager, "RecipeManager is null")
                .getRecipes()
                .stream()
                .filter(Objects::nonNull)
                .map(RecipeHolder::id)
                .filter(r -> modId.equals(r.getNamespace()))
                .toList();
    }

    @Override
    public Iterable<ResourceLocation> iterate(List<ResourceLocation> resourceLocations) {
        return resourceLocations;
    }

    @Override
    public ElementsBuilder<List<ResourceLocation>, ResourceLocation> newElementsBuilder() {
        return new AbstractElementsBuilder<>() {
            @Override
            public List<ResourceLocation> buildFromElementsList(List<List<ResourceLocation>> lists) {
                return List.copyOf(lists.stream().flatMap(Collection::stream).toList());
            }

            @Override
            public List<ResourceLocation> buildFromElementSet(Set<ResourceLocation> resourceLocations) {
                return List.copyOf(resourceLocations);
            }

            @Override
            public List<ResourceLocation> buildFromBoth(List<List<ResourceLocation>> lists, Set<ResourceLocation> resourceLocations) {
                return List.copyOf(Stream
                        .concat(lists.stream().flatMap(Collection::stream), resourceLocations.stream())
                        .toList());
            }
        };
    }

    public static class RecipeData {
    }
}
