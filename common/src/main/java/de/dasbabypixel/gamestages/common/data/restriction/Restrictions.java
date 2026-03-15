package de.dasbabypixel.gamestages.common.data.restriction;

import de.dasbabypixel.gamestages.common.data.restriction.predicates.*;
import org.jspecify.annotations.NonNull;

public class Restrictions {
    public static @NonNull PreparedRestrictionPredicate and(@NonNull PreparedRestrictionPredicate @NonNull ... dependencies) {
        return And.INSTANCE.prepare(dependencies);
    }

    public static @NonNull PreparedRestrictionPredicate or(@NonNull PreparedRestrictionPredicate @NonNull ... dependencies) {
        return Or.INSTANCE.prepare(dependencies);
    }

    public static @NonNull PreparedRestrictionPredicate not(@NonNull PreparedRestrictionPredicate other) {
        return Not.INSTANCE.prepare(other);
    }

    public static @NonNull PreparedRestrictionPredicate alwaysTrue() {
        return True.INSTANCE.prepare();
    }

    public static @NonNull PreparedRestrictionPredicate alwaysFalse() {
        return False.INSTANCE.prepare();
    }
}
