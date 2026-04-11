package de.dasbabypixel.gamestages.common.data.restriction.compiled;

import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CompiledRestrictionEntry<Self extends CompiledRestrictionEntry<Self, P>, P extends RestrictionEntry.PreCompiled<P, Self>> {
    P preCompiled();

    default RestrictionEntryOrigin origin() {
        return preCompiled().origin();
    }

    default TypedGameContent gameContent() {
        return preCompiled().gameContent();
    }
}
