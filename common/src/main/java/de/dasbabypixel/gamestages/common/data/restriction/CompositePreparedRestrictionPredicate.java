package de.dasbabypixel.gamestages.common.data.restriction;

import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@NullMarked
public record CompositePreparedRestrictionPredicate(RestrictionPredicate predicate,
                                                    List<PreparedRestrictionPredicate> dependencies) implements PreparedRestrictionPredicate {
    public CompositePreparedRestrictionPredicate {
        if (!predicate.accepts(dependencies)) throw new IllegalStateException();
        dependencies = Objects.requireNonNull(List.copyOf(dependencies));
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        predicate.append(sb, dependencies);
        return sb.toString();
    }
}
