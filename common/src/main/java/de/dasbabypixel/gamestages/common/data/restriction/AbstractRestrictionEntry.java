package de.dasbabypixel.gamestages.common.data.restriction;

import org.jspecify.annotations.NonNull;

public abstract class AbstractRestrictionEntry<T extends AbstractRestrictionEntry<T, P>, P> implements RestrictionEntry<T, P> {
    private final @NonNull RestrictionEntryOrigin origin;
    private boolean allowDuplicates = false;

    public AbstractRestrictionEntry(@NonNull RestrictionEntryOrigin origin) {
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
    public @NonNull RestrictionEntryOrigin origin() {
        return origin;
    }

    public boolean doesAllowDuplicates() {
        return allowDuplicates;
    }
}
