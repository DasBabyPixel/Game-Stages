package de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.messages;

import de.dasbabypixel.gamestages.common.data.manager.mutable.compiler.ManagerCompilerTask;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.predicates.True;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ResolveItemStackPredicate {
    public static final String ID = "resolve_item_stack_predicate";
    private final ManagerCompilerTask manager;
    private final @Nullable Ingredient ingredient;
    private final ItemStack itemStack;
    private PreparedRestrictionPredicate predicate = True.INSTANCE.prepare();

    public ResolveItemStackPredicate(ManagerCompilerTask manager, ItemStack itemStack, @Nullable Ingredient ingredient) {
        this.manager = manager;
        this.itemStack = itemStack;
        this.ingredient = ingredient;
    }

    public ManagerCompilerTask manager() {
        return manager;
    }

    public ItemStack itemStack() {
        return itemStack;
    }

    public @Nullable Ingredient ingredient() {
        return ingredient;
    }

    public PreparedRestrictionPredicate predicate() {
        return predicate;
    }

    public void predicate(PreparedRestrictionPredicate predicate) {
        this.predicate = predicate;
    }
}
