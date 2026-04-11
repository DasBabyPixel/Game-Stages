package de.dasbabypixel.gamestages.common.addons.fluid;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.AbstractRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AbstractFluidRestrictionEntry<T extends AbstractFluidRestrictionEntry<T, P>, P> extends AbstractRestrictionEntry<T, P> implements FluidRestrictionEntry<T, P> {
    private final GameContent targetFluids;
    private final PreparedRestrictionPredicate predicate;
    private boolean hideInJEI = true;

    public AbstractFluidRestrictionEntry(PreparedRestrictionPredicate predicate, RestrictionEntryOrigin origin, GameContent targetFluids) {
        super(origin);
        this.predicate = predicate;
        this.targetFluids = targetFluids;
    }

    public PreparedRestrictionPredicate predicate() {
        return predicate;
    }

    @Override
    public GameContent targetFluids() {
        return targetFluids;
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
