package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei;

import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.PlayerStages;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddon;
import org.jspecify.annotations.NonNull;

public class JEIAddon implements NeoAddon {
    public static final JEIAddon ADDON = new JEIAddon();

    private JEIAddon() {
    }

    @Override
    public void postCompileAll(@NonNull AbstractGameStageManager instance, @NonNull PlayerStages stages) {
        if (instance instanceof ClientGameStageManager) {
            singleRefreshAll(instance, stages);

            for (var addon : StagesJEIPlugin.addons()) {
                addon.postCompileAll(instance, stages);
            }
            System.out.println("Client postcompile done");
            System.out.println("Client postcompile done");
            System.out.println("Client postcompile done");
            System.out.println("Client postcompile done");
            System.out.println("Client postcompile done");
        } else {
            System.out.println("Ignoring server postCompile");
            System.out.println("Ignoring server postCompile");
            System.out.println("Ignoring server postCompile");
            System.out.println("Ignoring server postCompile");
            System.out.println("Ignoring server postCompile");
        }

//        iterate(stages, CommonFluidCollection.TYPE, entry -> {
//            if (entry instanceof NeoFluidRestrictionEntry.Compiled(var e, var gameContent, var predicate)) {
//                if (!e.hideInJEI()) return;
//                predicate.addNotifier(newTest -> updateVisibility(newTest, gameContent.fluids(), StagesJEIPlugin::showFluids, StagesJEIPlugin::hideFluids));
//            }
//        });
//        iterate(stages, CommonRecipeCollection.TYPE, entry -> {
//            if (entry instanceof NeoRecipeRestrictionEntry.Compiled(var e, var gameContent, var predicate)) {
//                if (!e.hideInJEI()) return;
//                predicate.addNotifier(newTest -> System.out.println(gameContent.content().size() + ": " + newTest));
//            }
//        });
    }

    public void singleRefreshAll(@NonNull AbstractGameStageManager instance, @NonNull PlayerStages stages) {
        for (var addon : StagesJEIPlugin.addons()) {
            addon.singleRefreshAll(instance, stages);
        }
//        iterate(stages, CommonFluidCollection.TYPE, entry -> {
//            if (entry instanceof NeoFluidRestrictionEntry.Compiled(var e, var gameContent, var predicate)) {
//                if (!e.hideInJEI()) return;
//                updateVisibility(predicate.test(), gameContent.fluids(), StagesJEIPlugin::showFluids, StagesJEIPlugin::hideFluids);
//            }
//        });
//        iterate(stages, CommonRecipeCollection.TYPE, entry -> {
//            if (entry instanceof NeoRecipeRestrictionEntry.Compiled(var e, var gameContent, var predicate)) {
//                if (!e.hideInJEI()) return;
//                System.out.println(gameContent.content().size() + ": " + predicate.test());
//            }
//        });
    }
}
