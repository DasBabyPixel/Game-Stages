package de.dasbabypixel.gamestages.common.v1_21_1.data.restriction.types;

import de.dasbabypixel.gamestages.common.addons.fluid.AbstractFluidRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import org.jspecify.annotations.NonNull;

public abstract class CommonFluidRestrictionEntry<T extends CommonFluidRestrictionEntry<T, P>, P> extends AbstractFluidRestrictionEntry<T, P> {
    public CommonFluidRestrictionEntry(@NonNull PreparedRestrictionPredicate predicate, @NonNull RestrictionEntryOrigin origin, @NonNull GameContent targetFluids) {
        super(predicate, origin, targetFluids);
    }
}
