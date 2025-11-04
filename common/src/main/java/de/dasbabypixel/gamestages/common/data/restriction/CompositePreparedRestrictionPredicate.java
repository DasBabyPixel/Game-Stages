package de.dasbabypixel.gamestages.common.data.restriction;

import org.jspecify.annotations.NonNull;

import java.util.List;

public record CompositePreparedRestrictionPredicate(@NonNull RestrictionPredicate predicate,
                                                    @NonNull List<PreparedRestrictionPredicate> dependencies) implements PreparedRestrictionPredicate {
    public CompositePreparedRestrictionPredicate {
        if (!predicate.accepts(dependencies)) throw new IllegalStateException();
        dependencies = List.copyOf(dependencies);
    }

    @Override
    public @NonNull String toString() {
        var sb = new StringBuilder();
        predicate.append(sb, dependencies);
        return sb.toString();
    }
}
