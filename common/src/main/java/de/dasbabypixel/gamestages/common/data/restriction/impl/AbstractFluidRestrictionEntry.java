package de.dasbabypixel.gamestages.common.data.restriction.impl;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.types.FluidRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.types.RestrictionEntryOrigin;
import org.jspecify.annotations.NonNull;

public abstract class AbstractFluidRestrictionEntry<T extends AbstractFluidRestrictionEntry<T, P>, P> extends AbstractRestrictionEntry<T, P> implements FluidRestrictionEntry<T, P> {
    private final @NonNull GameContent targetFluids;
    private boolean hideInJEI = true;

    public AbstractFluidRestrictionEntry(@NonNull PreparedRestrictionPredicate predicate, @NonNull RestrictionEntryOrigin origin, @NonNull GameContent targetFluids) {
        super(predicate, origin);
        this.targetFluids = targetFluids;
    }

    @Override
    public @NonNull GameContent targetFluids() {
        return targetFluids;
    }

    @Override
    public @NonNull T setHideInJEI(boolean hideInJEI) {
        this.hideInJEI = hideInJEI;
        return self();
    }

    public boolean hideInJEI() {
        return hideInJEI;
    }
}
