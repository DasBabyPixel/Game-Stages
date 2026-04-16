package de.dasbabypixel.gamestages.common.v1_21_1.data.restriction.types;

import de.dasbabypixel.gamestages.common.addons.fluid.AbstractFluidRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class CommonFluidRestrictionEntry<T extends CommonFluidRestrictionEntry<T, P, C>, P extends RestrictionEntry.PreCompiled<P, C>, C extends CompiledRestrictionEntry<C, P>> extends AbstractFluidRestrictionEntry<T, P, C> {
    public CommonFluidRestrictionEntry(PreparedRestrictionPredicate predicate, RestrictionEntryOrigin origin, TypedGameContent targetFluids) {
        super(predicate, origin, targetFluids);
    }
}
