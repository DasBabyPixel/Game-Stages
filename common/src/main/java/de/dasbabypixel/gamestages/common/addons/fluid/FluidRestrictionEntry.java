package de.dasbabypixel.gamestages.common.addons.fluid;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NonNull;

public interface FluidRestrictionEntry<T extends FluidRestrictionEntry<T, P>, P> extends RestrictionEntry<T, P> {
    @NonNull GameContent targetFluids();

    @NonNull T setHideInJEI(boolean hideInJEI);
}
