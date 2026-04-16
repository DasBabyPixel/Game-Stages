package de.dasbabypixel.gamestages.common.data.restriction;

import de.dasbabypixel.gamestages.common.data.GameStage;
import org.jspecify.annotations.NullMarked;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;

import java.util.Objects;

@NullMarked
public interface PreparedRestrictionPredicate {
    RestrictionPredicate predicate();

    default PreparedRestrictionPredicate and(PreparedRestrictionPredicate other) {
        return Restrictions.and(this, other);
    }

    default PreparedRestrictionPredicate or(PreparedRestrictionPredicate other) {
        return Restrictions.or(this, other);
    }

    default PreparedRestrictionPredicate not() {
        return Restrictions.not(this);
    }

    default Formula convertToLogicNG(FormulaFactory factory) {
        return switch (this) {
            case GameStage s -> Objects.requireNonNull(factory.variable(s.name()));
            case CompositePreparedRestrictionPredicate c -> c.predicate()
                    .convertToLogicNG(factory, c.dependencies()
                            .stream()
                            .map(s -> s.convertToLogicNG(factory))
                            .toArray(Formula[]::new));
            default -> throw new IllegalStateException("Unexpected value: " + this);
        };
    }
}
