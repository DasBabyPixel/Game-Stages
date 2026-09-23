package de.dasbabypixel.gamestages.common.addons.item;

import de.dasbabypixel.gamestages.common.data.GameContentWrapper;
import de.dasbabypixel.gamestages.common.data.restriction.AbstractRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AbstractItemRestrictionEntry<T extends AbstractItemRestrictionEntry<T, P, C>, P extends RestrictionEntry.PreCompiled<P, C>, C extends CompiledRestrictionEntry<C, P>> extends AbstractRestrictionEntry<T, P, C> implements ItemRestrictionEntry<T, P, C> {

    public AbstractItemRestrictionEntry(RestrictionEntryOrigin origin, GameContentWrapper.Direct gameContent) {
        super(origin, gameContent);
    }

    @Override
    public GameContentWrapper.Direct gameContent() {
        return (GameContentWrapper.Direct) super.gameContent();
    }
}
