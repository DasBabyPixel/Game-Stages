package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.GameStage;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Set;

public class CompositeStages extends BaseStages {
    private final @NonNull Set<@NonNull PlayerStages> dependencies;
    private boolean valid = false;

    public CompositeStages(@NonNull Set<@NonNull PlayerStages> dependencies) {
        super(Set.of());
        this.dependencies = dependencies;
    }

    public @NonNull Set<@NonNull PlayerStages> dependencies() {
        return dependencies;
    }

    @Override
    protected @NonNull Set<@NonNull GameStage> getUnlockedStages() {
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
