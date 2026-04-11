package de.dasbabypixel.gamestages.common.data.restriction;

import de.dasbabypixel.gamestages.common.data.restriction.predicates.*;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class Restrictions {
    public static PreparedRestrictionPredicate and(PreparedRestrictionPredicate... dependencies) {
        return And.INSTANCE.prepare(dependencies);
    }

    public static PreparedRestrictionPredicate or(PreparedRestrictionPredicate... dependencies) {
        return Or.INSTANCE.prepare(dependencies);
    }

    public static PreparedRestrictionPredicate not(PreparedRestrictionPredicate other) {
        return Not.INSTANCE.prepare(other);
    }

    public static PreparedRestrictionPredicate alwaysTrue() {
        return True.INSTANCE.prepare();
    }

    public static PreparedRestrictionPredicate alwaysFalse() {
        return False.INSTANCE.prepare();
    }
}
