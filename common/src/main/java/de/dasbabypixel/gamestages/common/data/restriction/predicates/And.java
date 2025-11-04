package de.dasbabypixel.gamestages.common.data.restriction.predicates;

import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionPredicate;
import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;

public final class And implements RestrictionPredicate {
    public static final And INSTANCE = new And();

    private And() {
    }

    @Override
    public boolean accepts(@NonNull List<? extends @NonNull PreparedRestrictionPredicate> dependencies) {
        return true;
    }

    @Override
    public boolean test(@NonNull List<? extends CompiledRestrictionPredicate> dependencies, @NonNull Player player) {
        for (var restriction : dependencies) {
            if (!restriction.test()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NonNull String toString() {
        return "&&";
    }
}
