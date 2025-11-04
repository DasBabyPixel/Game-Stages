package de.dasbabypixel.gamestages.common.data.restriction.compiled;

import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.NonNull;

public sealed interface CompiledRestrictionPredicate permits CachedCompiledRestrictionPredicate {
    @NonNull
    Player targetPlayer();

    boolean test();

    void invalidate();

    void addNotifier(@NonNull UpdateNotifier updateNotifier);

    interface UpdateNotifier {
        void update(boolean newTest);
    }
}
