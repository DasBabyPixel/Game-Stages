package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.server.MutableGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.RecipeIndex;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.VRecipeAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.*;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.server.RegisterEventJS;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class NeoRecipeAddon extends VRecipeAddon implements NeoAddon {
    @Override
    public void handle(CommonRecipeRestrictionPacket packet) {
        var entry = new NeoRecipeRestrictionEntry(packet.predicate(), RestrictionEntryOrigin.string(packet.origin()), packet.targetCollection());
        ClientGameStageManager.instance().addRestriction(entry);
    }

    @Override
    public void initResources(ReloadableServerResources serverResources, RegistryAccess registryAccess) {
        CommonRecipeCollection.recipeManager = serverResources.getRecipeManager();
    }

    @Override
    public void beforeRegisterEvent(AbstractGameStageManager<?> gameStageManager, ReloadableServerResources serverResources, RegistryAccess registryAccess) {
//        gameStageManager.get(RECIPE_INDEX).recipeIndex = recipeIndex;
//        System.out.println(recipeTree.findRelated(Items.OAK_PLANKS).size());
    }

    @Override
    public void afterRegisterEvent(AbstractGameStageManager<?> gameStageManager, ReloadableServerResources serverResources, RegistryAccess registryAccess) {
        NeoAddon.super.afterRegisterEvent(gameStageManager, serverResources, registryAccess);
    }

    @Override
    public void postReload(MutableGameStageManager gameStageManager, ReloadableServerResources serverResources, RegistryAccess registryAccess) {
        super.postReload(gameStageManager, serverResources, registryAccess);
        var recipeIndex = new RecipeIndex(serverResources.getRecipeManager(), this, gameStageManager, registryAccess);
        gameStageManager.get(RECIPE_INDEX).recipeIndex = recipeIndex;
        var deps = recipeIndex.getImplicitDependencies(ResourceLocation.parse("minecraft:oak_planks"));
        System.out.println("Deps: " + deps);
        System.out.println("Deps: " + deps);
        System.out.println("Deps: " + deps);
        System.out.println("Deps: " + deps);
    }

    @Override
    public NeoAddonKJS createKubeJSSupport() {
        return new KJS();
    }

    @Override
    public NeoAddonJEI createJEISupport() {
        return new RecipeJEI();
    }

    @Override
    public NeoAddonProbeJS createProbeJSSupport() {
        return new RecipeProbeJS();
    }

    private static class KJS implements NeoAddonKJS {
        private final RecipeJSParser recipeParser = new RecipeJSParser();

        @Override
        public void registerEventExtensions(EventRegistry registry) {
            var type = registry.get(RegisterEventJS.class);
            type.addFunctionVarArgs("recipes", (event, cx, args) -> args[0], RecipeCollectionWrapper.class, RecipeCollectionWrapper.class, RecipeCollectionWrapper[].class);
            type.addFunctionVarArgs("restrictRecipes", (event, cx, args) -> {
                var recipesContent = ((RecipeCollectionWrapper) args[1]).content();
                var predicate = (PreparedRestrictionPredicate) args[0];
                var source = Objects.requireNonNull(SourceLine.of(cx)).toString();
                return event
                        .stageManager()
                        .addRestriction(new NeoRecipeRestrictionEntry(predicate, RestrictionEntryOrigin.string(source), recipesContent));
            }, RecipeCollectionWrapper.class, NeoRecipeRestrictionEntry.class, PreparedRestrictionPredicate.class, RecipeCollectionWrapper[].class);
        }

        @Override
        public void registerTypeWrappers(TypeWrapperRegistry registry) {
            registry.register(RecipeCollectionWrapper.class, (TypeWrapperRegistry.ContextFromFunction<RecipeCollectionWrapper>) (context, o) -> new RecipeCollectionWrapper(recipeParser.parse(Objects.requireNonNull(context), Objects.requireNonNull(o))));
        }
    }
}
