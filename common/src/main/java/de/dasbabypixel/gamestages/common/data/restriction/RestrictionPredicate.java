package de.dasbabypixel.gamestages.common.data.restriction;

import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;

public interface RestrictionPredicate {
    default @NonNull CompositePreparedRestrictionPredicate prepare() {
        return prepare(List.of());
    }

    default @NonNull CompositePreparedRestrictionPredicate prepare(@NonNull PreparedRestrictionPredicate dependency1) {
        return prepare(List.of(dependency1));
    }

    default @NonNull CompositePreparedRestrictionPredicate prepare(@NonNull PreparedRestrictionPredicate dependency1, @NonNull PreparedRestrictionPredicate dependency2) {
        return prepare(List.of(dependency1, dependency2));
    }

    default @NonNull CompositePreparedRestrictionPredicate prepare(@NonNull PreparedRestrictionPredicate @NonNull ... dependencies) {
        return prepare(List.of(dependencies));
    }

    default @NonNull CompositePreparedRestrictionPredicate prepare(@NonNull List<PreparedRestrictionPredicate> dependencies) {
        return new CompositePreparedRestrictionPredicate(this, dependencies);
    }

    boolean accepts(@NonNull List<? extends @NonNull PreparedRestrictionPredicate> dependencies);

    boolean test(@NonNull List<? extends @NonNull CompiledRestrictionPredicate> dependencies, @NonNull Player player);

    @Override
    @NonNull
    String toString();

    default void append(@NonNull StringBuilder builder, @NonNull List<? extends @NonNull PreparedRestrictionPredicate> dependencies) {
        for (var i = 0; i < dependencies.size(); i++) {
            var dependency = dependencies.get(i);
            builder.append('(').append(dependency).append(')');
            if (i != dependencies.size() - 1) {
                builder.append(' ').append(this).append(' ');
            }
        }
    }
}
