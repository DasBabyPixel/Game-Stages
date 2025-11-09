package de.dasbabypixel.gamestages.common.data.restriction.compiled;

import de.dasbabypixel.gamestages.common.data.restriction.RestrictionPredicate;
import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

final class CachedCompiledRestrictionPredicate implements CompiledRestrictionPredicate {
    private final @NonNull Player player;
    private final @NonNull RestrictionPredicate predicate;
    private final @NonNull List<@NonNull CachedCompiledRestrictionPredicate> dependencies;
    private final @NonNull List<@NonNull UpdateNotifier> updateNotifiers = new ArrayList<>(0);
    private boolean cached = false;
    private boolean cachedOldValue = false;
    private boolean cachedValue;

    CachedCompiledRestrictionPredicate(@NonNull Player player, @NonNull RestrictionPredicate predicate, @NonNull List<@NonNull CachedCompiledRestrictionPredicate> dependencies) {
        this.player = player;
        this.predicate = predicate;
        this.dependencies = dependencies;
    }

    @Override
    public void addNotifier(@NonNull UpdateNotifier updateNotifier) {
        updateNotifiers.add(updateNotifier);
    }

    @Override
    public @NonNull Player targetPlayer() {
        return player;
    }

    @Override
    public boolean test() {
        if (cached) return cachedValue;
        cached = true;
        var oldValue = cachedValue;
        cachedValue = predicate.test(dependencies, player);
        if (cachedOldValue && cachedValue == oldValue) {
            return cachedValue;
        }
        for (var updateNotifier : updateNotifiers) {
            updateNotifier.update(cachedValue);
        }
        return cachedValue;
    }

    @Override
    public void invalidate() {
        if (cached) {
            cached = false;
            cachedOldValue = true;
            test();
            cachedOldValue = false;
        }
        test();
    }
}
