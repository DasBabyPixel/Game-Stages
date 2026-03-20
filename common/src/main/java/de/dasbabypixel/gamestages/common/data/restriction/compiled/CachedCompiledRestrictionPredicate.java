package de.dasbabypixel.gamestages.common.data.restriction.compiled;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionPredicate;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

final class CachedCompiledRestrictionPredicate implements CompiledRestrictionPredicate {
    private final @NonNull BaseStages stages;
    private final @NonNull PreparedRestrictionPredicate original;
    private final @NonNull RestrictionPredicate predicate;
    private final @NonNull List<@NonNull CachedCompiledRestrictionPredicate> dependencies;
    private final @NonNull List<@NonNull UpdateNotifier> updateNotifiers = new ArrayList<>(0);
    private boolean cached = false;
    private boolean cachedOldValue = false;
    private boolean cachedValue;

    CachedCompiledRestrictionPredicate(@NonNull BaseStages stages, @NonNull PreparedRestrictionPredicate original, @NonNull RestrictionPredicate predicate, @NonNull List<@NonNull CachedCompiledRestrictionPredicate> dependencies) {
        this.stages = stages;
        this.original = original;
        this.predicate = predicate;
        this.dependencies = dependencies;
    }

    @Override
    public void addNotifier(@NonNull UpdateNotifier updateNotifier) {
        updateNotifiers.add(updateNotifier);
    }

    @Override
    public PreparedRestrictionPredicate predicate() {
        return original;
    }

    @Override
    public @NonNull BaseStages stages() {
        return stages;
    }

    @Override
    public boolean test() {
        if (cached) return cachedValue;
        cached = true;
        var oldValue = cachedValue;
        cachedValue = predicate.test(dependencies, stages);
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
