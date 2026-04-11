package de.dasbabypixel.gamestages.common.data.restriction;

import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AbstractRestrictionEntry<T extends AbstractRestrictionEntry<T, P>, P> implements RestrictionEntry<T, P> {
    private final RestrictionEntryOrigin origin;
    private boolean allowDuplicates = false;

    public AbstractRestrictionEntry(RestrictionEntryOrigin origin) {
        this.origin = origin;
    }

    @Override
    public T disallowDuplicates() {
        allowDuplicates = false;
        return self();
    }

    @Override
    public T allowDuplicates() {
        allowDuplicates = true;
        return self();
    }

    @Override
    public RestrictionEntryOrigin origin() {
        return origin;
    }

    public boolean doesAllowDuplicates() {
        return allowDuplicates;
    }
}
