package de.dasbabypixel.gamestages.common.data.restriction.compiled;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionPredicate;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
final class CachedCompiledRestrictionPredicate implements CompiledRestrictionPredicate {
    private final BaseStages stages;
    private final PreparedRestrictionPredicate original;
    private final RestrictionPredicate predicate;
    private final List<CachedCompiledRestrictionPredicate> dependencies;
    private final List<UpdateNotifier> updateNotifiers = new ArrayList<>(0);
    private boolean cached = false;
    private boolean cachedOldValue = false;
    private boolean cachedValue;

    CachedCompiledRestrictionPredicate(BaseStages stages, PreparedRestrictionPredicate original, RestrictionPredicate predicate, List<CachedCompiledRestrictionPredicate> dependencies) {
        this.stages = stages;
        this.original = original;
        this.predicate = predicate;
        this.dependencies = dependencies;
    }

    @Override
    public void addNotifier(UpdateNotifier updateNotifier) {
        test();
        updateNotifiers.add(updateNotifier);
    }

    @Override
    public PreparedRestrictionPredicate predicate() {
        return original;
    }

    @Override
    public BaseStages stages() {
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
