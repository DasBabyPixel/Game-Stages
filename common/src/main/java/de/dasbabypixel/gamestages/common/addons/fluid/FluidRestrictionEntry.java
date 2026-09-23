package de.dasbabypixel.gamestages.common.addons.fluid;

import de.dasbabypixel.gamestages.common.data.GameContentWrapper;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface FluidRestrictionEntry<T extends FluidRestrictionEntry<T, P, C>, P extends RestrictionEntry.PreCompiled<P, C>, C extends CompiledRestrictionEntry<C, P>> extends RestrictionEntry<T, P, C> {
    @Override
    GameContentWrapper.Direct gameContent();

    T setHideInJEI(boolean hideInJEI);
}
