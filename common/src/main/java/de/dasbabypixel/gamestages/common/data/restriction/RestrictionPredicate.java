package de.dasbabypixel.gamestages.common.data.restriction;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import org.jspecify.annotations.NullMarked;

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
        return new CompositePreparedRestrictionPredicate(this, optimize(dependencies));
    }

    default List<PreparedRestrictionPredicate> optimize(List<PreparedRestrictionPredicate> list) {
        return list;
    }

    boolean accepts(List<? extends PreparedRestrictionPredicate> dependencies);

    boolean test(List<? extends CompiledRestrictionPredicate> dependencies, BaseStages stages);

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
