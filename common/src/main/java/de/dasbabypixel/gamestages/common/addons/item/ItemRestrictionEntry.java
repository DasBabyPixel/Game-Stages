package de.dasbabypixel.gamestages.common.addons.item;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NonNull;

public interface ItemRestrictionEntry<T extends ItemRestrictionEntry<T, P>, P> extends RestrictionEntry<T, P> {
    @NonNull GameContent targetItems();
}
