package de.dasbabypixel.gamestages.common.data.restriction.predicates;

import de.dasbabypixel.gamestages.common.data.restriction.CompositePreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record False() implements RestrictionPredicate {
    public static final False INSTANCE = new False();
    public static final PreparedRestrictionPredicate PREPARED = new CompositePreparedRestrictionPredicate(INSTANCE, List.of());

    @Override
    public boolean accepts(@NonNull List<? extends @NonNull PreparedRestrictionPredicate> dependencies) {
        return dependencies.isEmpty();
    }

    @Override
    public boolean test(@NonNull List<? extends @NonNull CompiledRestrictionPredicate> dependencies, @NonNull Player player) {
        return false;
    }

    @Override
    public @NonNull PreparedRestrictionPredicate prepare(@NonNull List<PreparedRestrictionPredicate> dependencies) {
        if (!INSTANCE.accepts(dependencies)) throw new IllegalStateException();
        return PREPARED;
    }

    @Override
    public @NonNull String toString() {
        return "false";
    }
}
