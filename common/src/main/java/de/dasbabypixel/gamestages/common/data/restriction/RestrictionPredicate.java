package de.dasbabypixel.gamestages.common.data.restriction;

import org.jspecify.annotations.NullMarked;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;

import java.util.List;

@NullMarked
public interface RestrictionPredicate {
    default PreparedRestrictionPredicate prepare() {
        return prepare(List.of());
    }

    default PreparedRestrictionPredicate prepare(PreparedRestrictionPredicate dependency1) {
        return prepare(List.of(dependency1));
    }

    default PreparedRestrictionPredicate prepare(PreparedRestrictionPredicate dependency1, PreparedRestrictionPredicate dependency2) {
        return prepare(List.of(dependency1, dependency2));
    }

    default PreparedRestrictionPredicate prepare(PreparedRestrictionPredicate... dependencies) {
        return prepare(List.of(dependencies));
    }

    default PreparedRestrictionPredicate prepare(List<PreparedRestrictionPredicate> dependencies) {
        return optimize(new CompositePreparedRestrictionPredicate(this, optimize(dependencies)));
    }

    default List<PreparedRestrictionPredicate> optimize(List<PreparedRestrictionPredicate> dependencies) {
        return dependencies;
    }

    default PreparedRestrictionPredicate optimize(CompositePreparedRestrictionPredicate predicate) {
        return predicate;
    }

    Formula convertToLogicNG(FormulaFactory factory, Formula[] dependencies);

    boolean equals(List<PreparedRestrictionPredicate> dependencies1, List<PreparedRestrictionPredicate> dependencies2);

    int hash(List<PreparedRestrictionPredicate> dependencies);

    boolean accepts(List<? extends PreparedRestrictionPredicate> dependencies);

    @Override
    String toString();

    default void append(StringBuilder builder, List<? extends PreparedRestrictionPredicate> dependencies) {
        if (dependencies.isEmpty()) {
            builder.append(this);
        }
        for (var i = 0; i < dependencies.size(); i++) {
            var dependency = dependencies.get(i);
            builder.append('(').append(dependency).append(')');
            if (i != dependencies.size() - 1) {
                builder.append(' ').append(this).append(' ');
            }
        }
    }
}
