package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionPredicate;
import org.jspecify.annotations.NullMarked;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;

import java.util.List;
import java.util.Objects;

@NullMarked
public record GameStage(String name) implements RestrictionPredicate, PreparedRestrictionPredicate {
    @Override
    public Formula convertToLogicNG(FormulaFactory factory, Formula[] dependencies) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Formula convertToLogicNG(FormulaFactory factory) {
        return Objects.requireNonNull(factory.variable(name));
    }

    @Override
    public boolean equals(List<PreparedRestrictionPredicate> dependencies1, List<PreparedRestrictionPredicate> dependencies2) {
        return true;
    }

    @Override
    public int hash(List<PreparedRestrictionPredicate> dependencies) {
        return hashCode();
    }

    @Override
    public boolean accepts(List<? extends PreparedRestrictionPredicate> dependencies) {
        return dependencies.isEmpty();
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public RestrictionPredicate predicate() {
        return this;
    }
}
