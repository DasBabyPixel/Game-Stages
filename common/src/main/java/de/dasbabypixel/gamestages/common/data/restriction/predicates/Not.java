package de.dasbabypixel.gamestages.common.data.restriction.predicates;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record Not() implements RestrictionPredicate {
    public static final Not INSTANCE = new Not();

    @Override
    public boolean accepts(List<? extends PreparedRestrictionPredicate> dependencies) {
        return dependencies.size() == 1;
    }

    @Override
    public boolean test(List<? extends CompiledRestrictionPredicate> dependencies, BaseStages stages) {
        return !dependencies.getFirst().test();
    }

    @Override
    public PreparedRestrictionPredicate prepare(List<PreparedRestrictionPredicate> dependencies) {
        if (!accepts(dependencies)) throw new IllegalStateException();
        var predicate = dependencies.getFirst().predicate();
        if (predicate instanceof True) return False.INSTANCE.prepare();
        if (predicate instanceof False) return True.INSTANCE.prepare();
        return RestrictionPredicate.super.prepare(dependencies);
    }

    @Override
    public String toString() {
        return "!";
    }

    @Override
    public void append(StringBuilder builder, List<? extends PreparedRestrictionPredicate> dependencies) {
        builder.append(this).append(dependencies.getFirst());
    }
}
