package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.manager.mutable.ClientMutableGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeRestrictionEntry;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.VRecipeAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.EventRegistry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonJEI;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonKJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.server.RegisterEventJS;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class NeoRecipeAddon extends VRecipeAddon implements NeoAddon {
    public NeoRecipeAddon() {
//        PRE_COMPILE_SERVER_PREPARE_EVENT.addListener(this::handle);
        INIT_RESOURCES_EVENT.addListener(this::handle);
    }

    @Override
    protected CommonRecipeRestrictionEntry createDefaultEntry(PreparedRestrictionPredicate predicate, CommonRecipeCollection recipes) {
        return new CommonRecipeRestrictionEntry(predicate, RestrictionEntryOrigin.SERVER, recipes);
    }

    @Override
    public void handle(CommonRecipeRestrictionPacket packet) {
        var entry = new CommonRecipeRestrictionEntry(packet.predicate(), RestrictionEntryOrigin.string(packet.origin()), packet.targetCollection());
        ClientMutableGameStageManager.buildingInstance().addRestriction(entry);
    }

    private void handle(InitResourcesEvent event) {
        CommonRecipeCollection.recipeManager = event.serverResources().getRecipeManager();
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
                var flattener = event.stageManager().get(GameContentFlattener.Attribute.INSTANCE);
                var recipesContent = flattener.flatten(((RecipeCollectionWrapper) args[1]).content(), CommonRecipeCollection.TYPE);
                var predicate = (PreparedRestrictionPredicate) args[0];
                var source = Objects.requireNonNull(SourceLine.of(cx)).toString();
                return event.stageManager()
                        .addRestriction(new CommonRecipeRestrictionEntry(predicate, RestrictionEntryOrigin.string(source), recipesContent));
            }, RecipeCollectionWrapper.class, CommonRecipeRestrictionEntry.class, PreparedRestrictionPredicate.class, RecipeCollectionWrapper[].class);
        }

        @Override
        public void registerTypeWrappers(TypeWrapperRegistry registry) {
            registry.register(RecipeCollectionWrapper.class, (TypeWrapperRegistry.ContextFromFunction<RecipeCollectionWrapper>) (context, o) -> new RecipeCollectionWrapper(recipeParser.parse(Objects.requireNonNull(context), Objects.requireNonNull(o))));
        }
    }
}
