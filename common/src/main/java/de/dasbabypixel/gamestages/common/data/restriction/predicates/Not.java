package de.dasbabypixel.gamestages.common.data.restriction.predicates;

import de.dasbabypixel.gamestages.common.data.restriction.CompositePreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionPredicate;
import org.jspecify.annotations.NullMarked;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;

import java.util.List;
import java.util.Objects;

@NullMarked
public record Not() implements RestrictionPredicate {
    public static final Not INSTANCE = new Not();

    @Override
    public boolean accepts(List<? extends PreparedRestrictionPredicate> dependencies) {
        return dependencies.size() == 1;
    }

    @Override
    public PreparedRestrictionPredicate prepare(List<PreparedRestrictionPredicate> dependencies) {
        if (!accepts(dependencies)) throw new IllegalStateException();
        var dep = dependencies.getFirst();
        var predicate = dep.predicate();
        return switch (predicate) {
            case True ignored -> False.INSTANCE.prepare();
            case False ignored -> True.INSTANCE.prepare();
            case Not ignored -> ((CompositePreparedRestrictionPredicate) dep).dependencies().getFirst();
            default -> RestrictionPredicate.super.prepare(dependencies);
        };
    }

    @Override
    public Formula convertToLogicNG(FormulaFactory factory, Formula[] dependencies) {
        if (dependencies.length != 1) throw new IllegalArgumentException();
        return Objects.requireNonNull(factory.not(dependencies[0]));
    }

    @Override
    public boolean equals(List<PreparedRestrictionPredicate> dependencies1, List<PreparedRestrictionPredicate> dependencies2) {
        return dependencies1.getFirst().equals(dependencies2.getFirst());
    }

    @Override
    public int hash(List<PreparedRestrictionPredicate> dependencies) {
        return Objects.hash(this, dependencies);
    }

    @Override
    public int hashCode() {
        return 0x13c0a42d;
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
