package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ServerGameStageManager;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;

@NullMarked
public class CompositeStages extends BaseStages {
    private final Set<PlayerStages> dependencies;
    private boolean valid = false;

    public CompositeStages(ServerGameStageManager manager, Set<PlayerStages> dependencies) {
        super(Set.of());
        this.dependencies = dependencies;
    }

    public Set<PlayerStages> dependencies() {
        return dependencies;
    }

    @Override
    public Set<GameStage> getUnlockedStages() {
        var set = super.getUnlockedStages();
        if (valid) return set;

        var old = Set.copyOf(set);
        set.clear();
        for (var dependency : dependencies) {
            set.addAll(dependency.getUnlockedStages());
        }
        var overlap = new HashSet<>(set);
        overlap.retainAll(old);
        var difference = new HashSet<>(set);
        difference.removeAll(overlap);
        valid = true;

        for (var gameStage : difference) {
            update(gameStage);
        }

        return set;
    }

    public void invalidate() {
        valid = false;
    }
}
