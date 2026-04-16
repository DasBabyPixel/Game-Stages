package de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.addon.ContentRegistry;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.attribute.Attribute;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VContentRegistry;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public abstract class VRecipeAddon implements VAddon {
    public static final Attribute<AbstractGameStageManager<?>, RecipeIndexHolder> RECIPE_INDEX = new Attribute<>(RecipeIndexHolder::new);
    private static @Nullable VRecipeAddon instance;

    public VRecipeAddon() {
        instance = this;
        REGISTER_CUSTOM_CONTENT_EVENT.addListener(this::handle);
        REGISTER_PACKETS_EVENT.addListener(this::handle);
        SERVER_BUILD_DEPENDENCY_GRAPH_EVENT.addListener(this::handle);
    }

    private void handle(RegisterCustomContentEvent event) {
        event.contentRegistry()
                .prepare(CommonRecipeCollection.TYPE)
                .set(ContentRegistry.NAME, "recipe")
                .set(ContentRegistry.FLATTENER_FACTORY, new RecipeFlattenerFactory())
                .set(VContentRegistry.GAME_CONTENT_SERIALIZER, CommonRecipeCollection.SERIALIZER)
                .register();
    }

    private void handle(ServerBuildDependencyGraphEvent event) {
        var graph = event.dependencyGraph();
    }

    private void handle(RegisterPacketsEvent event) {
        var registry = event.registry();
        registry.playClientBound(CommonRecipeRestrictionPacket.TYPE, CommonRecipeRestrictionPacket.STREAM_CODEC);
    }

    public abstract void handle(CommonRecipeRestrictionPacket packet);

    public static VRecipeAddon instance() {
        return Objects.requireNonNull(instance);
    }

    public static final class RecipeIndexHolder {
        public @Nullable RecipeIndex recipeIndex = null;

        public RecipeIndex index() {
            return Objects.requireNonNull(recipeIndex);
        }
    }
}
