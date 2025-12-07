package de.dasbabypixel.gamestages.common.data.restriction.impl;

import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.types.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.types.RestrictionEntryOrigin;
import org.jspecify.annotations.NonNull;

public abstract class AbstractRestrictionEntry<T extends AbstractRestrictionEntry<T, P>, P> implements RestrictionEntry<T, P> {
    private final @NonNull PreparedRestrictionPredicate predicate;
    private final @NonNull RestrictionEntryOrigin origin;
    private boolean allowDuplicates = false;

    public AbstractRestrictionEntry(@NonNull PreparedRestrictionPredicate predicate, @NonNull RestrictionEntryOrigin origin) {
        this.predicate = predicate;
        this.origin = origin;
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

    @Override
    public @NonNull RestrictionEntryOrigin origin() {
        return origin;
    }

    public boolean doesAllowDuplicates() {
        return allowDuplicates;
    }
}
