package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.data.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.GameContentWrapper;
import de.dasbabypixel.gamestages.common.data.manager.mutable.ClientMutableGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeRestrictionEntry;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.RecipeContentWrapper;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.RecipeType;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.VRecipeAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.EventRegistry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonKJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonProbeJS;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.jei.RecipeJEI;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei.JEIIntegration;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.StagesKubeJSPlugin;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.server.ServerRegisterEventJS;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class NeoRecipeAddon extends VRecipeAddon implements NeoAddon {
    public NeoRecipeAddon() {
        INIT_RESOURCES_EVENT.addListener(this::handle);
        JEIIntegration.INIT_JEI_SUPPORT_EVENT.addListener(this::initJEISupport);
    }

    @Override
    protected CommonRecipeRestrictionEntry createDefaultEntry(PreparedRestrictionPredicate predicate, RecipeContentWrapper recipes) {
        return new CommonRecipeRestrictionEntry(predicate, RestrictionEntryOrigin.SERVER, recipes);
    }

    @Override
    public void handle(CommonRecipeRestrictionPacket packet) {
        var entry = new CommonRecipeRestrictionEntry(packet.predicate(), RestrictionEntryOrigin.string(packet.origin()), packet.targetCollection());
        ClientMutableGameStageManager.buildingInstance().addRestriction(entry);
    }

    private void handle(InitResourcesEvent event) {
        ((RecipeType) RecipeType.get().type()).recipeManager = event.serverResources().getRecipeManager();
    }

    @Override
    public NeoAddonKJS createKubeJSSupport() {
        return new KJS();
    }

    public void initJEISupport(JEIIntegration.InitJEISupportEvent event) {
        RecipeJEI.init();
    }

    @Override
    public NeoAddonProbeJS createProbeJSSupport() {
        return new RecipeProbeJS();
    }

    private static class KJS implements NeoAddonKJS {
        private final RecipeJSParser recipeParser = new RecipeJSParser();

        {
            StagesKubeJSPlugin.register(RecipeType.get(), recipeParser::parse);
        }

        @Override
        public void registerEventExtensions(EventRegistry registry) {
            var type = registry.get(ServerRegisterEventJS.class);
            var recipeTypeArray = recipeParser.param(RecipeType.get());
            type.addFunctionVarArgs("recipes", recipeParser::parse, RecipeType.get(), recipeTypeArray);
            type.addFunctionVarArgs("restrictRecipes", (call, cx, args) -> {
                var event = call.event();
                var flattener = event.stageManager().get(GameContentFlattener.MUTABLE_MANAGER_ATTRIBUTE);
                var recipesContent = new RecipeContentWrapper(flattener.flatten(((GameContentWrapper) args[1]).gameContent(), RecipeType.get()));
                var predicate = (PreparedRestrictionPredicate) args[0];
                return event
                        .stageManager()
                        .addRestriction(new CommonRecipeRestrictionEntry(predicate, cx.origin(), recipesContent));
            }, CommonRecipeRestrictionEntry.class, PreparedRestrictionPredicate.class, recipeTypeArray);
        }
    }
}
