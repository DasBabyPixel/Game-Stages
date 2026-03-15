package de.dasbabypixel.gamestages.common.data.restriction;

import org.jspecify.annotations.NonNull;

public interface PreparedRestrictionPredicate {
    @NonNull RestrictionPredicate predicate();

    default @NonNull PreparedRestrictionPredicate and(@NonNull PreparedRestrictionPredicate other) {
        return Restrictions.and(this, other);
    }

    default @NonNull PreparedRestrictionPredicate or(@NonNull PreparedRestrictionPredicate other) {
        return Restrictions.or(this, other);
    }

    default @NonNull PreparedRestrictionPredicate not() {
        return Restrictions.not(this);
    }
}
