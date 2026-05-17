package de.dasbabypixel.gamestages.common.data.restriction;

import org.jspecify.annotations.NullMarked;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;

@NullMarked
public interface PreparedRestrictionPredicate {
    RestrictionPredicate predicate();

    default PreparedRestrictionPredicate and(PreparedRestrictionPredicate other) {
        return Restrictions.and(this, other);
    }

    default PreparedRestrictionPredicate or(PreparedRestrictionPredicate other) {
        return Restrictions.or(this, other);
    }

    default PreparedRestrictionPredicate not() {
        return Restrictions.not(this);
    }

    default Formula convertToLogicNG(FormulaFactory factory) {
        if (this instanceof CompositePreparedRestrictionPredicate c) {
            return c
                    .predicate()
                    .convertToLogicNG(factory, c
                            .dependencies()
                            .stream()
                            .map(s -> s.convertToLogicNG(factory))
                            .toArray(Formula[]::new));
        }
        throw new IllegalStateException("Unexpected value: " + this);
    }
}
