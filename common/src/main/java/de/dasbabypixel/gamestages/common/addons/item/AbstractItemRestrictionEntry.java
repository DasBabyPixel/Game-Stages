package de.dasbabypixel.gamestages.common.addons.item;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.AbstractRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AbstractItemRestrictionEntry<T extends AbstractItemRestrictionEntry<T, P>, P> extends AbstractRestrictionEntry<T, P> implements ItemRestrictionEntry<T, P> {
    private final GameContent targetItems;

    public AbstractItemRestrictionEntry(RestrictionEntryOrigin origin, GameContent targetItems) {
        super(origin);
        this.targetItems = targetItems;
    }

    @Override
    public GameContent targetItems() {
        return targetItems;
    }
}
