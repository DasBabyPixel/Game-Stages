package de.dasbabypixel.gamestages.common.data.restriction;

import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@NullMarked
public record CompositePreparedRestrictionPredicate(RestrictionPredicate predicate,
                                                    List<PreparedRestrictionPredicate> dependencies,
                                                    int hash) implements PreparedRestrictionPredicate {
    public CompositePreparedRestrictionPredicate(RestrictionPredicate predicate, List<PreparedRestrictionPredicate> dependencies) {
        this(predicate, dependencies, predicate.hash(dependencies));
    }

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

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof CompositePreparedRestrictionPredicate c && c.predicate.equals(predicate) && predicate.equals(dependencies, c.dependencies);
    }
}
