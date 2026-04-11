package de.dasbabypixel.gamestages.common.v1_21_1.data.restriction.types;

import de.dasbabypixel.gamestages.common.addons.fluid.AbstractFluidRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class CommonFluidRestrictionEntry<T extends CommonFluidRestrictionEntry<T, P>, P> extends AbstractFluidRestrictionEntry<T, P> {
    public CommonFluidRestrictionEntry(PreparedRestrictionPredicate predicate, RestrictionEntryOrigin origin, GameContent targetFluids) {
        super(predicate, origin, targetFluids);
    }
}
