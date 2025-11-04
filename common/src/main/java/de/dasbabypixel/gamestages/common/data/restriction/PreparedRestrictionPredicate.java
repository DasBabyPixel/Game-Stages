package de.dasbabypixel.gamestages.common.data.restriction;

import org.jspecify.annotations.NonNull;

public interface PreparedRestrictionPredicate {
    @NonNull RestrictionPredicate predicate();

    default @NonNull CompositePreparedRestrictionPredicate and(@NonNull PreparedRestrictionPredicate other) {
        return Restrictions.and(this, other);
    }

    default @NonNull CompositePreparedRestrictionPredicate or(@NonNull PreparedRestrictionPredicate other) {
        return Restrictions.or(this, other);
    }
}
