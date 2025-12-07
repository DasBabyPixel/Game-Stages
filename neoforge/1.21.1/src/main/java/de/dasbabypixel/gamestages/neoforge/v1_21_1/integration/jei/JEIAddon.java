package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.PlayerStages;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.data.restriction.types.NeoItemRestrictionEntry;
import org.jspecify.annotations.NonNull;

public class JEIAddon implements AbstractGameStageManager.Addon {
    public static final JEIAddon ADDON = new JEIAddon();

    private JEIAddon() {
    }

    @Override
    public void postCompile(@NonNull CompiledRestrictionEntry restrictionEntry) {
        handleEntry(restrictionEntry);
    }

    @Override
    public void postCompileAll(@NonNull AbstractGameStageManager instance, @NonNull PlayerStages stages) {

    }

    @Override
    public void clientPostSyncUnlockedStages(PlayerStages playerStages) {

    }

    private void handleEntry(CompiledRestrictionEntry restrictionEntry) {
        if (restrictionEntry instanceof NeoItemRestrictionEntry.Compiled(var items, var predicate)) {
            if (predicate.test()) {
                StagesJEIPlugin.show(items.items());
            } else {
                StagesJEIPlugin.hide(items.items());
            }
            predicate.addNotifier(newTest -> {
                if (newTest) {
                    StagesJEIPlugin.show(items.items());
                } else {
                    StagesJEIPlugin.hide(items.items());
                }
            });
        }
    }
}
