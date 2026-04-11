package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolver;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.CommonItemCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.CommonItemRestrictionEntry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonJEI;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@NullMarked
public class ItemJEI implements NeoAddonJEI {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemJEI.class);
    private final Map<Item, List<ItemStack>> itemCache = new HashMap<>();
    private boolean cachePopulated = false;
    private @Nullable IJeiRuntime runtime;

    public ItemJEI() {
    }

    private Map<Item, List<ItemStack>> getItemCache() {
        if (!cachePopulated) populateCache();
        return Objects.requireNonNull(itemCache);
    }

    private void populateCache() {
        Objects.requireNonNull(runtime);
        cachePopulated = true;
        var ingredientManager = runtime.getIngredientManager();
        for (var ingredient : ingredientManager.getAllIngredients(VanillaTypes.ITEM_STACK)) {
            assert ingredient != null;
            itemCache.computeIfAbsent(ingredient.getItem(), unused -> new ArrayList<>(1)).add(ingredient);
        }
        itemCache.entrySet().forEach(e -> {
            assert e != null;
            e.setValue(List.copyOf(Objects.requireNonNull(e.getValue())));
        });
    }

    private void clearCache() {
        cachePopulated = false;
        itemCache.clear();
    }

    @Override
    public void singleRefreshAll(AbstractGameStageManager instance, BaseStages stages) {
        if (runtime == null) {
            LOGGER.warn("JEI runtime missing when refreshing game stages");
            return;
        }
        iterate(stages, CommonItemCollection.TYPE, entry -> {
            if (entry instanceof CommonItemRestrictionEntry.Compiled(var ignored, var gameContent, var resolver)) {
                updateItems(gameContent.items(), resolver);
            }
        });
    }

    @Override
    public void postCompileAll(AbstractGameStageManager instance, BaseStages stages) {
        var byEntry = new HashMap<CompiledItemStackRestrictionEntry, List<ItemStack>>();
        iterate(stages, CommonItemCollection.TYPE, entry -> {
            if (entry instanceof CommonItemRestrictionEntry.Compiled(var ignoredE, var gameContent, var resolver)) {
                var items = getItems(gameContent.items());
                for (var item : items) {
                    var resolved = resolver.resolveRestrictionEntry(item);
                    if (resolved != null) {
                        var list = byEntry.computeIfAbsent(resolved, ignored -> new ArrayList<>());
                        list.add(item);
                    }
                }
            }
        });
        for (var entry : byEntry.entrySet()) {
            var list = Objects.requireNonNull(entry).getValue();
            entry.getKey().predicate().addNotifier(newTest -> {
                var r = runtime;
                if (r == null) return;
                if (newTest) {
                    r.getIngredientManager().addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, list);
                } else {
                    r.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, list);
                }
            });
        }
    }

    private List<ItemStack> getItems(HolderSet<Item> itemSet) {
        var itemCache = getItemCache();
        return itemSet
                .stream()
                .map(Objects::requireNonNull)
                .map(Holder::value)
                .map(itemCache::get)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .toList();
    }

    private void updateItems(HolderSet<Item> itemSet, ItemStackRestrictionResolver resolver) {
        Objects.requireNonNull(runtime);
        List<ItemStack> items = getItems(itemSet);
        if (items.isEmpty()) return;
        var showItems = new ArrayList<ItemStack>();
        var hideItems = new ArrayList<ItemStack>();
        for (var item : items) {
            var resolved = resolver.resolveRestrictionEntry(item);
            if (resolved == null || resolved.predicate().test()) {
                showItems.add(item);
            } else {
                hideItems.add(item);
            }
        }
        if (!showItems.isEmpty()) {
            runtime.getIngredientManager().addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, showItems);
        }
        if (!hideItems.isEmpty()) {
            runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hideItems);
        }
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
