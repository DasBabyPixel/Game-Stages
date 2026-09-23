package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.jei;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.CommonItemRestrictionEntry;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.ItemType;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.client.ContentVisibilityUpdater;
import mezz.jei.api.constants.VanillaTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@NullMarked
public class ItemVisibilityUpdater extends ContentVisibilityUpdater<ItemVisibilityUpdater.Entry, ItemStack, CommonItemRestrictionEntry.Compiled> {
    private final ItemJEI itemJEI;

    public ItemVisibilityUpdater(ItemJEI itemJEI) {
        super(ItemType.get());
        this.itemJEI = itemJEI;
    }

    @Override
    protected boolean shouldBeVisible(Entry entry) {
        return entry.predicate.test();
    }

    @Override
    protected void collect(BaseStages stages, BaseStages.CompileIndex compileIndex, CommonItemRestrictionEntry.Compiled compiled, Collector collector) {
        var itemSet = compiled.gameContent().gameContent().elements();
        var resolver = compiled.resolver();

        List<ItemStack> items = getItems(itemSet);
        if (items.isEmpty()) return;
        for (var item : items) {
            var resolved = resolver.resolveRestrictionEntry(item);

            if (resolved != null) {
                resolved.settings();
                var predicate = resolved.predicate();
                collector.add(predicate, item);
            }
        }
    }

    private List<ItemStack> getItems(HolderSet<Item> itemSet) {
        var itemCache = itemJEI.getItemCache();
        return itemSet
                .stream()
                .map(Objects::requireNonNull)
                .map(Holder::value)
                .map(itemCache::get)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .toList();
    }

    @Override
    protected void show(List<ItemStack> show) {
        itemJEI.runtime().getIngredientManager().addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, show);
    }

    @Override
    protected void hide(List<ItemStack> hide) {
        itemJEI.runtime().getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hide);
    }

    @Override
    protected ItemStack extract(Entry entry) {
        return entry.stack;
    }

    @Override
    protected Entry createWrapper(ItemStack itemStack, List<CompiledRestrictionPredicate> relevantEntries) {
        if (relevantEntries.size() != 1) throw new IllegalStateException();
        var predicate = relevantEntries.getFirst();
        return new Entry(itemStack, predicate);
    }

    public static class Entry {
        private final ItemStack stack;
        private final CompiledRestrictionPredicate predicate;

        public Entry(ItemStack stack, CompiledRestrictionPredicate predicate) {
            this.stack = stack;
            this.predicate = predicate;
        }
    }
}
