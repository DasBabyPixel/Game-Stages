package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record GameStage(String name) implements RestrictionPredicate, PreparedRestrictionPredicate {
    @Override
    public boolean accepts(@NonNull List<? extends @NonNull PreparedRestrictionPredicate> dependencies) {
        return dependencies.isEmpty();
    }

    @Override
    public boolean test(@NonNull List<? extends CompiledRestrictionPredicate> dependencies, @NonNull BaseStages stages) {
        return stages.hasUnlocked(this);
    }

    @Override
    public @NonNull String toString() {
        return name();
    }

    @Override
    public @NonNull RestrictionPredicate predicate() {
        return this;
    }
}
