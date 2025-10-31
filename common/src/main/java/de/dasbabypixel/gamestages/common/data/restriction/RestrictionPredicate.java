package de.dasbabypixel.gamestages.common.data.restriction;

import de.dasbabypixel.gamestages.common.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;

public interface RestrictionPredicate {
    default @NonNull PreparedRestrictionPredicate prepare() {
        return prepare(List.of());
    }

    default @NonNull PreparedRestrictionPredicate prepare(@NonNull PreparedRestrictionPredicate dependency1) {
        return prepare(List.of(dependency1));
    }

    default @NonNull PreparedRestrictionPredicate prepare(@NonNull PreparedRestrictionPredicate dependency1, @NonNull PreparedRestrictionPredicate dependency2) {
        return prepare(List.of(dependency1, dependency2));
    }

    default @NonNull PreparedRestrictionPredicate prepare(@NonNull PreparedRestrictionPredicate @NonNull ... dependencies) {
        return prepare(List.of(dependencies));
    }

    default @NonNull PreparedRestrictionPredicate prepare(@NonNull List<PreparedRestrictionPredicate> dependencies) {
        return new PreparedRestrictionPredicate(this, dependencies);
    }

    boolean accepts(@NonNull List<@NonNull PreparedRestrictionPredicate> dependencies);

    boolean test(@NonNull List<? extends @NonNull CompiledRestrictionPredicate> dependencies, @NonNull Player player);

    @Override
    @NonNull String toString();

    default void append(@NonNull StringBuilder builder, @NonNull List<@NonNull PreparedRestrictionPredicate> dependencies) {
        for (var i = 0; i < dependencies.size(); i++) {
            var dependency = dependencies.get(i);
            builder.append('(').append(dependency).append(')');
            if (i != dependencies.size() - 1) {
                builder.append(' ').append(this).append(' ');
            }
        }
    }
}
