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
public record False() implements RestrictionPredicate {
    public static final False INSTANCE = new False();
    public static final PreparedRestrictionPredicate PREPARED = new CompositePreparedRestrictionPredicate(INSTANCE, List.of());

    @Override
    public boolean accepts(List<? extends PreparedRestrictionPredicate> dependencies) {
        return dependencies.isEmpty();
    }

    @Override
    public PreparedRestrictionPredicate prepare(List<PreparedRestrictionPredicate> dependencies) {
        if (!INSTANCE.accepts(dependencies)) throw new IllegalStateException();
        return PREPARED;
    }

    @Override
    public Formula convertToLogicNG(FormulaFactory factory, Formula[] dependencies) {
        return Objects.requireNonNull(factory.falsum());
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
    public int hashCode() {
        return 0x4b82caa7;
    }

    @Override
    public String toString() {
        return "false";
    }
}
