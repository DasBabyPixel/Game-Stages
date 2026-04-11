package de.dasbabypixel.gamestages.common.data.restriction.predicates;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.restriction.CompositePreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record True() implements RestrictionPredicate {
    public static final True INSTANCE = new True();
    public static final PreparedRestrictionPredicate PREPARED = new CompositePreparedRestrictionPredicate(INSTANCE, List.of());

    @Override
    public boolean accepts(List<? extends PreparedRestrictionPredicate> dependencies) {
        return dependencies.isEmpty();
    }

    @Override
    public boolean test(List<? extends CompiledRestrictionPredicate> dependencies, BaseStages stages) {
        return true;
    }

    @Override
    public PreparedRestrictionPredicate prepare(List<PreparedRestrictionPredicate> dependencies) {
        if (!INSTANCE.accepts(dependencies)) throw new IllegalStateException();
        return PREPARED;
    }

    @Override
    public String toString() {
        return "true";
    }
}
