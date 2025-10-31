package de.dasbabypixel.gamestages.common.data.restriction;

import org.jspecify.annotations.NonNull;

import java.util.List;

public record PreparedRestrictionPredicate(@NonNull RestrictionPredicate predicate,
                                           @NonNull List<PreparedRestrictionPredicate> dependencies) {
    public PreparedRestrictionPredicate {
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
