package de.dasbabypixel.gamestages.common.v1_21_1.data.restriction.types;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.impl.AbstractItemRestrictionEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public abstract class CommonItemRestrictionEntry<T extends CommonItemRestrictionEntry<T, P>, P> extends AbstractItemRestrictionEntry<T, P> {
    private @Nullable Function<ItemStack, Component> hiddenName;
    private @Nullable Function<ItemStack, Component> dropMessage;
    private @Nullable Function<ItemStack, Component> attackMessage;
    private @Nullable Function<ItemStack, Component> pickupMessage;
    private @Nullable Function<ItemStack, Component> usageMessage;
    private @Nullable Function<ItemStack, Component> breakMessage;
    private @Nullable Function<ItemStack, Component> placeMessage;
    private @Nullable Function<ItemStack, Component> jadeItemMessage;
    private @Nullable Function<ItemStack, Component> jadeBlockMessage;

    public CommonItemRestrictionEntry(@NonNull PreparedRestrictionPredicate predicate, @NonNull GameContent targetItems) {
        super(predicate, targetItems);
    }

    public @Nullable Function<ItemStack, Component> hiddenName() {
        return hiddenName;
    }

    public @Nullable Function<ItemStack, Component> dropMessage() {
        return dropMessage;
    }

    public @Nullable Function<ItemStack, Component> attackMessage() {
        return attackMessage;
    }

    public @Nullable Function<ItemStack, Component> pickupMessage() {
        return pickupMessage;
    }

    public @Nullable Function<ItemStack, Component> usageMessage() {
        return usageMessage;
    }

    public @Nullable Function<ItemStack, Component> breakMessage() {
        return breakMessage;
    }

    public @Nullable Function<ItemStack, Component> placeMessage() {
        return placeMessage;
    }

    public @Nullable Function<ItemStack, Component> jadeItemMessage() {
        return jadeItemMessage;
    }

    public @Nullable Function<ItemStack, Component> jadeBlockMessage() {
        return jadeBlockMessage;
    }

    public T setHiddenName(Function<ItemStack, Component> hiddenName) {
        this.hiddenName = hiddenName;
        return self();
    }

    public T setDropMessage(Function<ItemStack, Component> dropMessage) {
        this.dropMessage = dropMessage;
        return self();
    }

    public T setAttackMessage(Function<ItemStack, Component> attackMessage) {
        this.attackMessage = attackMessage;
        return self();
    }

    public T setPickupMessage(Function<ItemStack, Component> pickupMessage) {
        this.pickupMessage = pickupMessage;
        return self();
    }

    public T setUsageMessage(Function<ItemStack, Component> usageMessage) {
        this.usageMessage = usageMessage;
        return self();
    }

    public T setBreakMessage(Function<ItemStack, Component> breakMessage) {
        this.breakMessage = breakMessage;
        return self();
    }

    public T setPlaceMessage(Function<ItemStack, Component> placeMessage) {
        this.placeMessage = placeMessage;
        return self();
    }

    public T setJadeItemMessage(Function<ItemStack, Component> jadeItemMessage) {
        this.jadeItemMessage = jadeItemMessage;
        return self();
    }

    public T setJadeBlockMessage(Function<ItemStack, Component> jadeBlockMessage) {
        this.jadeBlockMessage = jadeBlockMessage;
        return self();
    }
}
