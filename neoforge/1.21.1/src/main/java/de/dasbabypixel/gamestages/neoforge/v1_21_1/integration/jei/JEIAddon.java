package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.PlayerStages;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonItemCollection;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.restriction.types.NeoItemRestrictionEntry;
import org.jspecify.annotations.NonNull;

public class JEIAddon implements AbstractGameStageManager.Addon {
    public static final JEIAddon ADDON = new JEIAddon();

    private JEIAddon() {
    }

    @Override
    public void postCompileAll(@NonNull AbstractGameStageManager instance, @NonNull PlayerStages stages) {
        singleRefreshAll(stages);
        var typeIndex = stages.typeIndexMap().get(CommonItemCollection.TYPE);
        if (typeIndex == null) return;
        for (var entry : typeIndex.contentListByEntry().entrySet()) {
            var restrictionEntry = entry.getKey();
            if (restrictionEntry instanceof NeoItemRestrictionEntry.Compiled compiled) {
                compiled.predicate().addNotifier(newTest -> {
                    var items = compiled.gameContent();
                    if (newTest) {
                        StagesJEIPlugin.show(items.items());
                    } else {
                        StagesJEIPlugin.hide(items.items());
                    }
                });
            }
        }
    }

    public void singleRefreshAll(@NonNull PlayerStages stages) {
        var typeIndex = stages.typeIndexMap().get(CommonItemCollection.TYPE);
        if (typeIndex == null) return;
        for (var entry : typeIndex.contentListByEntry().entrySet()) {
            var restrictionEntry = entry.getKey();
            if (restrictionEntry instanceof NeoItemRestrictionEntry.Compiled compiled) {
                if (compiled.predicate().test()) {
                    StagesJEIPlugin.show(compiled.gameContent().items());
                } else {
                    StagesJEIPlugin.hide(compiled.gameContent().items());
                }
            }
        }
    }
}
