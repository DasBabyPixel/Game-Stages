package de.dasbabypixel.gamestages.common.data.restriction;

import org.jspecify.annotations.NullMarked;

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
}
