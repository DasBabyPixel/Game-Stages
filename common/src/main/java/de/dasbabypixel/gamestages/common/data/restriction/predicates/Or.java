package de.dasbabypixel.gamestages.common.data.restriction.predicates;

import de.dasbabypixel.gamestages.common.data.restriction.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionPredicate;
import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;

public final class Or implements RestrictionPredicate {
    public static final Or INSTANCE = new Or();

    private Or() {
    }

    @Override
    public boolean accepts(@NonNull List<@NonNull PreparedRestrictionPredicate> dependencies) {
        return true;
    }

    @Override
    public boolean test(@NonNull List<? extends CompiledRestrictionPredicate> dependencies, @NonNull Player player) {
        for (var restriction : dependencies) {
            if (restriction.test()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @NonNull String toString() {
        return "||";
    }
}
