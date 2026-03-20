package de.dasbabypixel.gamestages.common.client;

import de.dasbabypixel.gamestages.common.addon.AddonManager;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClientPlayerStages extends BaseStages {
    @Override
    protected Set<GameStage> fetchUnlockedStages() {
        return Set.of();
    }

    public void syncUnlockedStages(List<GameStage> gameStages) {
        var updated = new HashSet<GameStage>();
        var keep = Set.copyOf(gameStages);
        var it = getUnlockedStages().iterator();
        while (it.hasNext()) {
            var stage = it.next();
            if (keep.contains(stage)) continue;
            it.remove();
            updated.add(stage);
        }
        for (var stage : gameStages) {
            if (getUnlockedStages().add(stage)) {
                updated.add(stage);
            }
        }
        updated.forEach(this::update);
        compiledGameStages.values().forEach(CompiledRestrictionPredicate::test);
        for (var addon : AddonManager.instance().addons()) {
            addon.clientPostSyncUnlockedStages(this);
        }
    }
}
