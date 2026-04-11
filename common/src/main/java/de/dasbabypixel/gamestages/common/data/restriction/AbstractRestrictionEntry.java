package de.dasbabypixel.gamestages.common.data.restriction;

import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AbstractRestrictionEntry<T extends AbstractRestrictionEntry<T, P, C>, P extends RestrictionEntry.PreCompiled<P, C>, C extends CompiledRestrictionEntry<C, P>> implements RestrictionEntry<T, P, C> {
    private final RestrictionEntryOrigin origin;

    public AbstractRestrictionEntry(RestrictionEntryOrigin origin) {
        this.origin = origin;
    }

    @Override
    public RestrictionEntryOrigin origin() {
        return origin;
    }
}
