package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.PlayerStages;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonFluidCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonGameContentType;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonItemCollection;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.restriction.types.NeoFluidRestrictionEntry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.restriction.types.NeoItemRestrictionEntry;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class JEIAddon implements AbstractGameStageManager.Addon {
    public static final JEIAddon ADDON = new JEIAddon();

    private JEIAddon() {
    }

    private static <T> void updateVisibility(boolean newVisibility, T content, Consumer<? super T> show, Consumer<? super T> hide) {
        if (newVisibility) {
            show.accept(content);
        } else {
            hide.accept(content);
        }
    }

    private static <T extends TypedGameContent> void iterate(PlayerStages stages, CommonGameContentType<T> type, Consumer<CompiledRestrictionEntry> consumer) {
        var typeIndex = stages.typeIndexMap().get(type);
        if (typeIndex == null) return;
        for (var entry : typeIndex.contentListByEntry().keySet()) {
            consumer.accept(entry);
        }
    }

    @Override
    public void postCompileAll(@NonNull AbstractGameStageManager instance, @NonNull PlayerStages stages) {
        singleRefreshAll(stages);

        iterate(stages, CommonItemCollection.TYPE, entry -> {
            if (entry instanceof NeoItemRestrictionEntry.Compiled(var e, var gameContent, var predicate)) {
                if (!e.hideInJEI()) return;
                predicate.addNotifier(newTest -> updateVisibility(newTest, gameContent.items(), StagesJEIPlugin::showItems, StagesJEIPlugin::hideItems));
            }
        });
        iterate(stages, CommonFluidCollection.TYPE, entry -> {
            if (entry instanceof NeoFluidRestrictionEntry.Compiled(var e, var gameContent, var predicate)) {
                if (!e.hideInJEI()) return;
                predicate.addNotifier(newTest -> updateVisibility(newTest, gameContent.fluids(), StagesJEIPlugin::showFluids, StagesJEIPlugin::hideFluids));
            }
        });
    }

    public void singleRefreshAll(@NonNull PlayerStages stages) {
        iterate(stages, CommonItemCollection.TYPE, entry -> {
            if (entry instanceof NeoItemRestrictionEntry.Compiled(var e, var gameContent, var predicate)) {
                if (!e.hideInJEI()) return;
                updateVisibility(predicate.test(), gameContent.items(), StagesJEIPlugin::showItems, StagesJEIPlugin::hideItems);
            }
        });
        iterate(stages, CommonFluidCollection.TYPE, entry -> {
            if (entry instanceof NeoFluidRestrictionEntry.Compiled(var e, var gameContent, var predicate)) {
                if (!e.hideInJEI()) return;
                updateVisibility(predicate.test(), gameContent.fluids(), StagesJEIPlugin::showFluids, StagesJEIPlugin::hideFluids);
            }
        });
    }
}
