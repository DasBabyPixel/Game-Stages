package de.dasbabypixel.gamestages.common.data.restriction.impl;

import de.dasbabypixel.gamestages.common.data.ItemCollection;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.types.ItemRestrictionEntry;
import org.jspecify.annotations.NonNull;

public abstract class AbstractItemRestrictionEntry<T extends AbstractItemRestrictionEntry<T, P>, P> extends AbstractRestrictionEntry<T, P> implements ItemRestrictionEntry<T, P> {
    private final @NonNull ItemCollection<?> targetCollection;
    private boolean canBeAttackedWith = false;
    private boolean canBeStoredInInventory = false;
    private boolean canBeStoredInContainers = false;
    private boolean canBeEquipped = false;
    private boolean canBePickedUp = false;
    private boolean hideTooltip = true;
    private boolean renderItemName = false;
    private boolean hideInJEI = true;
    private boolean canBePlaced = false;
    private boolean canItemBeLeftClicked = false;
    private boolean canItemBeRightClicked = false;
    private boolean canInteractWithBlock = false;
    private boolean canBeDugWith = false;

    public AbstractItemRestrictionEntry(@NonNull PreparedRestrictionPredicate predicate, @NonNull ItemCollection<?> targetCollection) {
        super(predicate);
        this.targetCollection = targetCollection;
    }

    @Override
    public @NonNull ItemCollection<?> targetCollection() {
        return targetCollection;
    }

    public boolean canBeAttackedWith() {
        return canBeAttackedWith;
    }

    public boolean canBeStoredInInventory() {
        return canBeStoredInInventory;
    }

    public boolean canBeStoredInContainers() {
        return canBeStoredInContainers;
    }

    public boolean canBeEquipped() {
        return canBeEquipped;
    }

    public boolean canBePickedUp() {
        return canBePickedUp;
    }

    public boolean hideTooltip() {
        return hideTooltip;
    }

    public boolean renderItemName() {
        return renderItemName;
    }

    public boolean hideInJEI() {
        return hideInJEI;
    }

    public boolean canBePlaced() {
        return canBePlaced;
    }

    public boolean canItemBeLeftClicked() {
        return canItemBeLeftClicked;
    }

    public boolean canItemBeRightClicked() {
        return canItemBeRightClicked;
    }

    public boolean canInteractWithBlock() {
        return canInteractWithBlock;
    }

    public boolean canBeDugWith() {
        return canBeDugWith;
    }

    @Override
    public @NonNull T setCanBeAttackedWith(boolean canBeAttackedWith) {
        this.canBeAttackedWith = canBeAttackedWith;
        return self();
    }

    @Override
    public @NonNull T setCanBeStoredInInventory(boolean canBeStoredInInventory) {
        this.canBeStoredInInventory = canBeStoredInInventory;
        return self();
    }

    @Override
    public @NonNull T setCanBeStoredInContainers(boolean canBeStoredInContainers) {
        this.canBeStoredInContainers = canBeStoredInContainers;
        return self();
    }

    @Override
    public @NonNull T setCanBeEquipped(boolean canBeEquipped) {
        this.canBeEquipped = canBeEquipped;
        return self();
    }

    @Override
    public @NonNull T setCanBePickedUp(boolean canBePickedUp) {
        this.canBePickedUp = canBePickedUp;
        return self();
    }

    @Override
    public @NonNull T setHideTooltip(boolean hideTooltip) {
        this.hideTooltip = hideTooltip;
        return self();
    }

    @Override
    public @NonNull T setRenderItemName(boolean renderItemName) {
        this.renderItemName = renderItemName;
        return self();
    }

    @Override
    public @NonNull T setHideInJEI(boolean hideInJEI) {
        this.hideInJEI = hideInJEI;
        return self();
    }

    @Override
    public @NonNull T setCanBePlaced(boolean canBePlaced) {
        this.canBePlaced = canBePlaced;
        return self();
    }

    @Override
    public @NonNull T setCanItemBeLeftClicked(boolean canItemBeLeftClicked) {
        this.canItemBeLeftClicked = canItemBeLeftClicked;
        return self();
    }

    @Override
    public @NonNull T setCanItemBeRightClicked(boolean canItemBeRightClicked) {
        this.canItemBeRightClicked = canItemBeRightClicked;
        return self();
    }

    @Override
    public @NonNull T setCanInteractWithBlock(boolean canInteractWithBlock) {
        this.canInteractWithBlock = canInteractWithBlock;
        return self();
    }

    @Override
    public @NonNull T setCanBeDugWith(boolean canDig) {
        this.canBeDugWith = canDig;
        return self();
    }
}
