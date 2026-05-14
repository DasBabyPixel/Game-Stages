package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.CommonItemCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.CommonItemRestrictionEntry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.addon.NeoAddonJEI;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.client.ContentVisibilityUpdater;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class ItemJEI implements NeoAddonJEI {
    private final Map<Item, List<ItemStack>> itemCache = new HashMap<>();
    private boolean cachePopulated = false;
    private @Nullable IJeiRuntime runtime;
    private final ContentVisibilityUpdater<ItemStack, CommonItemRestrictionEntry.Compiled> updater = new ContentVisibilityUpdater<>(CommonItemCollection.TYPE) {
        @Override
        protected void collect(BaseStages stages, BaseStages.CompileIndex compileIndex, CommonItemRestrictionEntry.Compiled compiled, Collector<ItemStack> collector) {
            var itemSet = compiled.gameContent().items();
            var resolver = compiled.resolver();

            Objects.requireNonNull(runtime);
            List<ItemStack> items = getItems(itemSet);
            if (items.isEmpty()) return;
            for (var item : items) {
                var resolved = resolver.resolveRestrictionEntry(item);
                if (resolved == null || resolved.predicate().test()) {
                    collector.show(item);
                } else {
                    collector.hide(item);
                }
            }
        }

        @Override
        protected void registerUpdateNotifier(BaseStages stages, BaseStages.CompileIndex compileIndex, List<CommonItemRestrictionEntry.Compiled> compiledEntries, UpdateRegistrar<ItemStack> registrar) {
            for (var compiledEntry : compiledEntries) {
                var items = getItems(compiledEntry.gameContent().items());
                for (var item : items) {
                    var resolved = compiledEntry.resolver().resolveRestrictionEntry(item);
                    if (resolved == null) continue;
                    var predicate = resolved.predicate();
                    registrar.register(predicate, item);
                }
            }
        }

        @Override
        protected void show(List<ItemStack> show) {
            Objects.requireNonNull(runtime)
                    .getIngredientManager()
                    .addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, show);
        }

        @Override
        protected void hide(List<ItemStack> hide) {
            Objects.requireNonNull(runtime)
                    .getIngredientManager()
                    .removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hide);
        }
    };

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
            e.setValue(Objects.requireNonNull(List.copyOf(Objects.requireNonNull(e.getValue()))));
        });
    }

    private void clearCache() {
        cachePopulated = false;
        itemCache.clear();
    }

    @Override
    public void jeiReloaded(ClientGameStageManager instance, BaseStages stages) {
        updater.fullReconfigure(stages);
    }

    private List<ItemStack> getItems(HolderSet<Item> itemSet) {
        var itemCache = getItemCache();
        return itemSet.stream()
                .map(Objects::requireNonNull)
                .map(Holder::value)
                .map(itemCache::get)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .toList();
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
