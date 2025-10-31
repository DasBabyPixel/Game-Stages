package de.dasbabypixel.gamestages.common.data.restriction;

import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

final class CachedCompiledRestrictionPredicate implements CompiledRestrictionPredicate {
    private final @NonNull Player player;
    private final @NonNull RestrictionPredicate predicate;
    private final @NonNull List<? extends CompiledRestrictionPredicate> dependencies;
    private final @NonNull List<CachedCompiledRestrictionPredicate> dependants = new ArrayList<>(0);
    private boolean cached = false;
    private boolean cachedValue;

    CachedCompiledRestrictionPredicate(@NonNull Player player, @NonNull RestrictionPredicate predicate, @NonNull List<@NonNull CachedCompiledRestrictionPredicate> dependencies) {
        this.player = player;
        this.predicate = predicate;
        this.dependencies = dependencies;
    }

    public void addDependant(@NonNull CachedCompiledRestrictionPredicate dependant) {
        dependants.add(dependant);
    }

    @Override
    public boolean test() {
        if (cached) return cachedValue;
        cached = true;
        return cachedValue = predicate.test(dependencies, player);
    }

    @Override
    public void invalidate() {
        if (!cached) return;
        cached = false;
        for (var dependant : dependants) {
            dependant.invalidate();
        }
    }
}
