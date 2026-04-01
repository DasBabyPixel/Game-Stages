package de.dasbabypixel.gamestages.common.data.restriction;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

public record CompositePreparedRestrictionPredicate(@NonNull RestrictionPredicate predicate,
                                                    @NonNull List<@NonNull PreparedRestrictionPredicate> dependencies) implements PreparedRestrictionPredicate {
    public CompositePreparedRestrictionPredicate {
        if (!predicate.accepts(dependencies)) throw new IllegalStateException();
        dependencies = Objects.requireNonNull(List.copyOf(dependencies));
    }

    @Override
    public @NonNull String toString() {
        var sb = new StringBuilder();
        predicate.append(sb, dependencies);
        return sb.toString();
    }
}
