package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.CommonItemCollection;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonJEI;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class ItemJEI implements NeoAddonJEI {
    private final Map<Item, List<ItemStack>> itemCache = new HashMap<>();
    private boolean cachePopulated = false;
    private IJeiRuntime runtime;

    public ItemJEI() {
    }

    private Map<Item, List<ItemStack>> getItemCache() {
        if (!cachePopulated) populateCache();
        return itemCache;
    }

    private void populateCache() {
        cachePopulated = true;
        var ingredientManager = runtime.getIngredientManager();
        for (var ingredient : ingredientManager.getAllIngredients(VanillaTypes.ITEM_STACK)) {
            itemCache.computeIfAbsent(ingredient.getItem(), unused -> new ArrayList<>(1)).add(ingredient);
        }
        itemCache.entrySet().forEach(e -> e.setValue(List.copyOf(e.getValue())));
    }

    private void clearCache() {
        cachePopulated = false;
        itemCache.clear();
    }

    @Override
    public void singleRefreshAll(@NonNull AbstractGameStageManager instance, @NonNull BaseStages stages) {
        iterate(stages, CommonItemCollection.TYPE, entry -> {
            if (entry instanceof NeoItemRestrictionEntry.Compiled(var e, var gameContent, var predicate)) {
                if (!e.hideInJEI()) return;
                updateVisibility(predicate.test(), gameContent.items(), this::showItems, this::hideItems);
            }
        });
    }

    @Override
    public void postCompileAll(@NonNull AbstractGameStageManager instance, @NonNull BaseStages stages) {
        iterate(stages, CommonItemCollection.TYPE, entry -> {
            if (entry instanceof NeoItemRestrictionEntry.Compiled(var e, var gameContent, var predicate)) {
                if (!e.hideInJEI()) return;
                predicate.addNotifier(newTest -> updateVisibility(newTest, gameContent.items(), this::showItems, this::hideItems));
            }
        });
    }

    public void showItems(HolderSet<Item> items) {
        var itemCache = getItemCache();
        runtime
                .getIngredientManager()
                .addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, items
                        .stream()
                        .map(Holder::value)
                        .map(itemCache::get)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .toList());
    }

    public void hideItems(HolderSet<Item> items) {
        var itemCache = getItemCache();
        runtime
                .getIngredientManager()
                .removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, items
                        .stream()
                        .map(Holder::value)
                        .map(itemCache::get)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .toList());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onRuntimeUnavailable() {
        clearCache();
        this.runtime = null;
    }
}
