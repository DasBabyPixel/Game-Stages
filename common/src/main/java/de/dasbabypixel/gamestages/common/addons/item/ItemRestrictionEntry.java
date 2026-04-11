package de.dasbabypixel.gamestages.common.addons.item;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ItemRestrictionEntry<T extends ItemRestrictionEntry<T, P, C>, P extends RestrictionEntry.PreCompiled<P, C>, C extends CompiledRestrictionEntry<C, P>> extends RestrictionEntry<T, P, C> {
    GameContent targetItems();

    @Override
    default GameContent gameContent() {
        return targetItems();
    }
}
