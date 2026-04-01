package de.dasbabypixel.gamestages.common.data.restriction.compiled;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import org.jspecify.annotations.NonNull;

public interface CompiledRestrictionEntry {
    @NonNull RestrictionEntryOrigin origin();

    @NonNull GameContent gameContent();
}
