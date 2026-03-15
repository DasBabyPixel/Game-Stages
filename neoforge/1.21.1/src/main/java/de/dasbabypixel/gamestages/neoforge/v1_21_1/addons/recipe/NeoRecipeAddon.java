package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.VRecipeAddon;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.*;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.event.RegisterEventJS;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.item.Items;

import static de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.KJSHelper.drop;

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
    public void beforeRegisterEvent(AbstractGameStageManager gameStageManager, ReloadableServerResources serverResources, RegistryAccess registryAccess) {
        var recipeTree = new NeoRecipeTree(serverResources.getRecipeManager(), registryAccess);
        System.out.println(recipeTree.findRelated(Items.OAK_PLANKS).size());
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
            var predicateType = TypeInfo.of(PreparedRestrictionPredicate.class);
            var type = registry.get(RegisterEventJS.class);
            type.addFunction("recipes", (event, cx, args) -> recipeParser.parse(cx, args));
            type.addFunction("restrictRecipes", (event, cx, args) -> {
                var recipesContent = recipeParser.parse(cx, drop(args, 1));
                var predicate = (PreparedRestrictionPredicate) cx.jsToJava(args[0], predicateType);
                var source = SourceLine.of(cx).toString();
                return event
                        .stageManager()
                        .addRestriction(new NeoRecipeRestrictionEntry(predicate, RestrictionEntryOrigin.string(source), recipesContent));
            });
        }

        @Override
        public void registerTypeWrappers(TypeWrapperRegistry registry) {
            registry.register(RecipeCollectionWrapper.class, (TypeWrapperRegistry.ContextFromFunction<RecipeCollectionWrapper>) (context, o) -> new RecipeCollectionWrapper(recipeParser.parse(context, o)));
        }
    }
}
