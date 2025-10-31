package de.dasbabypixel.gamestages.common.data.restriction;

import de.dasbabypixel.gamestages.common.data.restriction.predicates.And;
import de.dasbabypixel.gamestages.common.data.restriction.predicates.Or;
import org.jspecify.annotations.NonNull;

public class Restrictions {
    public static @NonNull PreparedRestrictionPredicate and(@NonNull PreparedRestrictionPredicate @NonNull ... dependencies) {
        return And.INSTANCE.prepare(dependencies);
    }

    public static @NonNull PreparedRestrictionPredicate or(@NonNull PreparedRestrictionPredicate @NonNull ... dependencies) {
        return Or.INSTANCE.prepare(dependencies);
    }
}
