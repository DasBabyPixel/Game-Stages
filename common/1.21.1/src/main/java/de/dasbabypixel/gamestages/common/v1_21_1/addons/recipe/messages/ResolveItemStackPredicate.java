package de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.messages;

import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.predicates.True;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ResolveItemStackPredicate {
    public static final String ID = "resolve_item_stack_predicate";
    public ItemStack itemStack;
    public @Nullable Ingredient ingredient;
    public PreparedRestrictionPredicate predicate = True.INSTANCE.prepare();

    public ResolveItemStackPredicate(ItemStack itemStack, @Nullable Ingredient ingredient) {
        this.itemStack = itemStack;
        this.ingredient = ingredient;
    }
}
