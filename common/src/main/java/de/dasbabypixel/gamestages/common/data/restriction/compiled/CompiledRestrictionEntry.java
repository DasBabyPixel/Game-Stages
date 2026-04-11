package de.dasbabypixel.gamestages.common.data.restriction.compiled;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CompiledRestrictionEntry {
    RestrictionEntryOrigin origin();

    GameContent gameContent();
}
