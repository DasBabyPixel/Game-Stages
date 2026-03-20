package de.dasbabypixel.gamestages.common.data.restriction.predicates;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record Not() implements RestrictionPredicate {
    public static final Not INSTANCE = new Not();

    @Override
    public boolean accepts(@NonNull List<? extends @NonNull PreparedRestrictionPredicate> dependencies) {
        return dependencies.size() == 1;
    }

    @Override
    public boolean test(@NonNull List<? extends @NonNull CompiledRestrictionPredicate> dependencies, @NonNull BaseStages stages) {
        return !dependencies.getFirst().test();
    }

    @Override
    public @NonNull PreparedRestrictionPredicate prepare(@NonNull List<PreparedRestrictionPredicate> dependencies) {
        if (!accepts(dependencies)) throw new IllegalStateException();
        var predicate = dependencies.getFirst().predicate();
        if (predicate instanceof True) return False.INSTANCE.prepare();
        if (predicate instanceof False) return True.INSTANCE.prepare();
        return RestrictionPredicate.super.prepare(dependencies);
    }

    @Override
    public @NonNull String toString() {
        return "!";
    }

    @Override
    public void append(@NonNull StringBuilder builder, @NonNull List<? extends @NonNull PreparedRestrictionPredicate> dependencies) {
        builder.append(this).append(dependencies.getFirst());
    }
}
