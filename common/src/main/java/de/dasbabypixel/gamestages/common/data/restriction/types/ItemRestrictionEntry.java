package de.dasbabypixel.gamestages.common.data.restriction.types;

import de.dasbabypixel.gamestages.common.data.ItemCollection;
import org.jspecify.annotations.NonNull;

public interface ItemRestrictionEntry<T extends ItemRestrictionEntry<T>> extends RestrictionEntry<T> {
    @NonNull ItemCollection targetCollection();

    @NonNull T setCanBeAttackedWith(boolean canBeAttackedWith);

    @NonNull T setCanBeStoredInInventory(boolean canBeStoredInInventory);

    @NonNull T setCanBeStoredInContainers(boolean canBeStoredInContainers);

    @NonNull T setCanBeEquipped(boolean canBeEquipped);

    @NonNull T setCanBePickedUp(boolean canBePickedUp);

    @NonNull T setHideTooltip(boolean hideTooltip);

    @NonNull T setRenderItemName(boolean renderItemName);

    @NonNull T setHideInJEI(boolean hideInJEI);

    @NonNull T setCanBePlaced(boolean canBePlaced);

    @NonNull T setCanItemBeLeftClicked(boolean canItemBeLeftClicked);

    @NonNull T setCanItemBeRightClicked(boolean canItemBeRightClicked);

    @NonNull T setCanInteractWithBlock(boolean canInteractWithBlock);

    @NonNull T setCanBeDugWith(boolean canDig);
}
