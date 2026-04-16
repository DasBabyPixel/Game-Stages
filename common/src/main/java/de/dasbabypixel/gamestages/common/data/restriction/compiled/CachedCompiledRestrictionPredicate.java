package de.dasbabypixel.gamestages.common.data.restriction.compiled;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.logicng.LogicNG;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import org.jspecify.annotations.NullMarked;
import org.logicng.datastructures.Assignment;
import org.logicng.formulas.Formula;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NullMarked
final class CachedCompiledRestrictionPredicate implements CompiledRestrictionPredicate {
    private final BaseStages stages;
    private final PreparedRestrictionPredicate original;
    private final LogicNG logicNG;
    private final Formula formula;
    private final List<CachedCompiledRestrictionPredicate> dependencies;
    private final List<UpdateNotifier> updateNotifiers = new ArrayList<>(0);
    private boolean cached = false;
    private boolean cachedOldValue = false;
    private boolean cachedValue;

    CachedCompiledRestrictionPredicate(LogicNG logicNG, BaseStages stages, PreparedRestrictionPredicate original, List<CachedCompiledRestrictionPredicate> dependencies) {
        this.stages = stages;
        this.original = original;
        this.dependencies = dependencies;
        this.logicNG = logicNG;
        this.formula = Objects.requireNonNull(logicNG.simplifier()
                .apply(original.convertToLogicNG(logicNG.formulaFactory()), true));
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
        var assignment = new Assignment(true);
        for (var unlockedStage : this.stages.getUnlockedStages()) {
            assignment.addLiteral(Objects.requireNonNull(logicNG.formulaFactory().variable(unlockedStage.name())));
        }
        cachedValue = formula.evaluate(assignment);
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
