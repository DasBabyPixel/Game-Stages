package de.dasbabypixel.gamestages.common.addons.fluid;

import de.dasbabypixel.gamestages.common.data.GameContentWrapper;
import de.dasbabypixel.gamestages.common.data.restriction.AbstractRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AbstractFluidRestrictionEntry<T extends AbstractFluidRestrictionEntry<T, P, C>, P extends RestrictionEntry.PreCompiled<P, C>, C extends CompiledRestrictionEntry<C, P>> extends AbstractRestrictionEntry<T, P, C> implements FluidRestrictionEntry<T, P, C> {
    private final PreparedRestrictionPredicate predicate;
    private boolean hideInJEI = true;

    public AbstractFluidRestrictionEntry(PreparedRestrictionPredicate predicate, RestrictionEntryOrigin origin, GameContentWrapper.Direct gameContent) {
        super(origin, gameContent);
        this.predicate = predicate;
    }

    public PreparedRestrictionPredicate predicate() {
        return predicate;
    }

    @Override
    public GameContentWrapper.Direct gameContent() {
        return (GameContentWrapper.Direct) super.gameContent();
    }

    @Override
    public T setHideInJEI(boolean hideInJEI) {
        this.hideInJEI = hideInJEI;
        return self();
    }

    public boolean hideInJEI() {
        return hideInJEI;
    }
}
