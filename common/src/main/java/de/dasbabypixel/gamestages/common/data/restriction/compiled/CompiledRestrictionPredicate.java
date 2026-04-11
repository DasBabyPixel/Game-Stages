package de.dasbabypixel.gamestages.common.data.restriction.compiled;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import org.jspecify.annotations.NullMarked;

@NullMarked
public sealed interface CompiledRestrictionPredicate permits CachedCompiledRestrictionPredicate {
    BaseStages stages();

    boolean test();

    void invalidate();

    void addNotifier(UpdateNotifier updateNotifier);

    PreparedRestrictionPredicate predicate();

    interface UpdateNotifier {
        void update(boolean newTest);
    }
}
