package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record GameStage(String name) implements RestrictionPredicate, PreparedRestrictionPredicate {
    @Override
    public boolean accepts(List<? extends PreparedRestrictionPredicate> dependencies) {
        return dependencies.isEmpty();
    }

    @Override
    public boolean test(List<? extends CompiledRestrictionPredicate> dependencies, BaseStages stages) {
        return stages.hasUnlocked(this);
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public RestrictionPredicate predicate() {
        return this;
    }
}
