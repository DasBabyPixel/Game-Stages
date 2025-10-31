package de.dasbabypixel.gamestages.common.data.restriction.impl;

import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.types.RestrictionEntry;
import org.jspecify.annotations.NonNull;

public abstract class AbstractRestrictionEntry<T extends AbstractRestrictionEntry<T>> implements RestrictionEntry<T> {
    private final @NonNull PreparedRestrictionPredicate predicate;
    private boolean allowDuplicates = false;

    public AbstractRestrictionEntry(@NonNull PreparedRestrictionPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public @NonNull T disallowDuplicates() {
        allowDuplicates = false;
        return self();
    }

    @Override
    public @NonNull T allowDuplicates() {
        allowDuplicates = true;
        return self();
    }

    @Override
    public @NonNull PreparedRestrictionPredicate predicate() {
        return predicate;
    }

    public boolean doesAllowDuplicates() {
        return allowDuplicates;
    }
}
