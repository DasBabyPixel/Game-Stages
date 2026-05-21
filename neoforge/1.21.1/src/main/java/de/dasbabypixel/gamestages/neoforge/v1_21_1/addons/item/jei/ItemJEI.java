package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.jei;

import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.jei.JEIAddon;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class ItemJEI {
    private static @Nullable ItemJEI instance;
    private final Map<Item, List<ItemStack>> itemCache = new HashMap<>();
    private final ItemVisibilityUpdater updater = new ItemVisibilityUpdater(this);
    private boolean cachePopulated = false;
    private @Nullable IJeiRuntime runtime;

    private ItemJEI() {
        JEIAddon.RUNTIME_AVAILABLE_EVENT.addListener(this::onRuntimeAvailable);
        JEIAddon.RUNTIME_UNAVAILABLE_EVENT.addListener(this::onRuntimeUnavailable);
    }

    public static void init() {
        if (instance != null) throw new IllegalStateException();
        instance = new ItemJEI();
    }

    public IJeiRuntime runtime() {
        return Objects.requireNonNull(runtime);
    }

    public Map<Item, List<ItemStack>> getItemCache() {
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

    public void onRuntimeAvailable(JEIAddon.RuntimeAvailableEvent event) {
        this.runtime = event.runtime();
        updater.viewerStartup();
    }

    public void onRuntimeUnavailable(JEIAddon.RuntimeUnavailableEvent event) {
        clearCache();
        this.runtime = null;
    }
}
