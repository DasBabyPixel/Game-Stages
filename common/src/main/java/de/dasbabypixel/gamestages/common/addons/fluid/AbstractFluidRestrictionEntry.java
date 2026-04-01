package de.dasbabypixel.gamestages.common.addons.fluid;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.AbstractRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import org.jspecify.annotations.NonNull;

public abstract class AbstractFluidRestrictionEntry<T extends AbstractFluidRestrictionEntry<T, P>, P> extends AbstractRestrictionEntry<T, P> implements FluidRestrictionEntry<T, P> {
    private final @NonNull GameContent targetFluids;
    private final @NonNull PreparedRestrictionPredicate predicate;
    private boolean hideInJEI = true;

    public AbstractFluidRestrictionEntry(@NonNull PreparedRestrictionPredicate predicate, @NonNull RestrictionEntryOrigin origin, @NonNull GameContent targetFluids) {
        super(origin);
        this.predicate = predicate;
        this.targetFluids = targetFluids;
    }

    public @NonNull PreparedRestrictionPredicate predicate() {
        return predicate;
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
