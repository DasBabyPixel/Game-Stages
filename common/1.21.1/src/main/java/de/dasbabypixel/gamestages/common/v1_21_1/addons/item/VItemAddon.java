package de.dasbabypixel.gamestages.common.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.addon.AddonManager;
import de.dasbabypixel.gamestages.common.addon.ContentRegistry;
import de.dasbabypixel.gamestages.common.addons.item.ItemAddon;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactories;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactory;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenCompiler;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.PacketRegistry;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VContentRegistry;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.predicate.PredicateCompiler;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.predicate.PredicateData;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class VItemAddon extends ItemAddon implements VAddon {
    private static VItemAddon instance;

    private final ItemRecipeIntegration recipeIntegration = new ItemRecipeIntegration();

    public VItemAddon() {
        instance = this;
        DataDrivenCompiler.instance().register("predicate", PredicateData.class, new PredicateCompiler());
    }

    @Override
    public void onRegister(@NonNull AddonManager<? extends Addon> addonManager) {
        recipeIntegration.register(addonManager);
    }

    @Override
    public void preReload(@NonNull AbstractGameStageManager instance) {
        VAddon.super.preReload(instance);
    }

    @Override
    public void postReload(@NonNull AbstractGameStageManager instance) {
        VAddon.super.postReload(instance);
    }

    @Override
    public void preCompileAll(@NonNull RecompilationTask recompilationTask) {
        var factoryContextMap = new HashMap<@NonNull ItemStackRestrictionResolverFactory<?>, Object>();
        for (var factory : ItemStackRestrictionResolverFactories.instance().getAll()) {
            factoryContextMap.put(factory, factory.createContext(recompilationTask));
        }
        recompilationTask.setContext(this, new ItemCompileContext(factoryContextMap, new CompilationContext()));

        super.preCompileAll(recompilationTask);
    }

    @Override
    public void postCompileAll(@NonNull RecompilationTask recompilationTask) {
        var itemMap = new HashMap<Holder<Item>, CompiledRestrictionEntry>();
        var flattener = recompilationTask.instance().get(GameContentFlattener.Attribute.INSTANCE);
        for (var value : recompilationTask.stages().compiledRestrictionEntryMap().values()) {
            var items = flattener.flatten(value.gameContent(), CommonItemCollection.TYPE);
            for (var item : items.items()) {
                itemMap.put(item, value);
            }
        }
        recompilationTask.stages().addonData().put(this, new ItemAddonData(itemMap));
        var context = (ItemCompileContext) Objects.requireNonNull(recompilationTask.getContext(this));
    }

    @Override
    public void registerCustomContent(@NonNull ContentRegistry registry) {
        registry
                .prepare(CommonItemCollection.TYPE)
                .set(ContentRegistry.NAME, "item")
                .set(ContentRegistry.FLATTENER_FACTORY, new ItemFlattenerFactory())
                .set(VContentRegistry.GAME_CONTENT_SERIALIZER, CommonItemCollection.SERIALIZER)
                .register();
    }

    @Override
    public void registerPackets(@NonNull PacketRegistry registry) {
        registry.playClientBound(CommonItemRestrictionPacket.TYPE, CommonItemRestrictionPacket.STREAM_CODEC);
    }

    public abstract void handle(@NonNull CommonItemRestrictionPacket packet);

    public static CompiledItemStackRestrictionEntry getEntry(@NonNull BaseStages stages, @NonNull ItemStack nmsItemStack, de.dasbabypixel.gamestages.common.data.@NonNull ItemStack ourItemStack) {
        var data = (ItemAddonData) stages.addonData().get(instance());
        if (data == null) return null;
        var entry = data.itemMap.get(nmsItemStack.getItemHolder());
        if (entry == null) return null;
        var resolver = ((CommonItemRestrictionEntry.Compiled) entry).resolver();
        return resolver.resolveRestrictionEntry(ourItemStack);
    }

    public static @NonNull VItemAddon instance() {
        return Objects.requireNonNull(instance);
    }

    public record ItemAddonData(@NonNull Map<@NonNull Holder<Item>, @NonNull CompiledRestrictionEntry> itemMap) {
    }

    public record ItemCompileContext(
            @NonNull Map<@NonNull ItemStackRestrictionResolverFactory<?>, Object> factoryContextMap,
            @NonNull CompilationContext compilationContext) implements RecompileContext {
    }
}
