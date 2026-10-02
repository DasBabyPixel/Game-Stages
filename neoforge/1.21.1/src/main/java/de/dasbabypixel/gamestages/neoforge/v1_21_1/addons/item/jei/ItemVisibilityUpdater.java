package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.jei;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.CommonItemRestrictionEntry;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.ItemType;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei.JEIVisibilityUpdater;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.ingredients.TypedIngredient;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class ItemVisibilityUpdater extends JEIVisibilityUpdater<ItemVisibilityUpdater.JEIContext, ItemVisibilityUpdater.ItemContext, ItemVisibilityUpdater.Entry, ItemStack, CommonItemRestrictionEntry.Compiled> {

    public ItemVisibilityUpdater() {
        super(ItemType.get());
    }

    @Override
    protected boolean shouldBeVisible(Entry entry) {
        return entry.predicate().test();
    }

    @Override
    protected void collect(JEIContext jeiContext, ItemContext itemContext, BaseStages stages, BaseStages.CompileIndex compileIndex, CommonItemRestrictionEntry.Compiled compiledEntry, Collector collector) {
        var itemSet = compiledEntry.gameContent().gameContent().elements();
        var resolver = compiledEntry.resolver();

        List<ItemStack> items = getItems(jeiContext, itemSet);
        if (items.isEmpty()) return;
        for (var item : items) {
            var resolved = resolver.resolveRestrictionEntry(item);

            if (resolved != null) {
                var predicate = resolved.predicate();
                collector.add(predicate, item);
            }
        }
    }

    private List<ItemStack> getItems(JEIContext jeiContext, HolderSet<Item> itemSet) {
        var itemCache = jeiContext.itemCache();
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
        context().runtime().getIngredientManager().addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, show);
    }

    @Override
    protected void hide(List<ItemStack> hide) {
        removeBookmarks(hide);
        context().runtime().getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hide);
    }

    private void removeBookmarks(List<ItemStack> items) {
        var ingredientManager = context().runtime().getIngredientManager();
        for (var itemStack : items) {
            var ingredient = TypedIngredient.createAndFilterInvalid(ingredientManager, VanillaTypes.ITEM_STACK, itemStack, false);
            if (ingredient == null) continue;
            context().runtime().getBookmarkManager().remove(ingredient);
        }
    }

    @Override
    protected ItemStack extract(Entry entry) {
        return entry.stack();
    }

    @Override
    protected Entry createWrapper(ItemStack itemStack, List<CompiledRestrictionPredicate> relevantEntries) {
        if (relevantEntries.size() != 1) throw new IllegalStateException();
        var predicate = relevantEntries.getFirst();
        return new Entry(itemStack, predicate);
    }

    @Override
    protected JEIContext createContext(IJeiRuntime runtime) {
        var itemCache = new HashMap<Item, List<ItemStack>>();
        var ingredientManager = runtime.getIngredientManager();
        for (var ingredient : ingredientManager.getAllIngredients(VanillaTypes.ITEM_STACK)) {
            assert ingredient != null;
            itemCache.computeIfAbsent(ingredient.getItem(), unused -> new ArrayList<>(1)).add(ingredient);
        }
        itemCache.entrySet().forEach(e -> {
            assert e != null;
            e.setValue(List.copyOf(e.getValue()));
        });

        return new JEIContext(runtime, itemCache);
    }

    @Override
    protected void initialize(JEIContext jeiContext, ItemContext itemContext) {

    }

    @Override
    protected void shutdown(JEIContext jeiContext, ItemContext itemContext) {

    }

    public record JEIContext(IJeiRuntime runtime, Map<Item, List<ItemStack>> itemCache) {
        public JEIContext {
            itemCache = Map.copyOf(itemCache);
        }
    }

    public record ItemContext() {
    }

    public record Entry(ItemStack stack, CompiledRestrictionPredicate predicate) {
    }
}
