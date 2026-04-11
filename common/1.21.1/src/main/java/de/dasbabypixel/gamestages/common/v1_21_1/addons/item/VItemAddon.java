package de.dasbabypixel.gamestages.common.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.addon.AddonManager;
import de.dasbabypixel.gamestages.common.addon.ContentRegistry;
import de.dasbabypixel.gamestages.common.addons.item.ItemAddon;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactories;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactory;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenCompiler;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.attribute.Attribute;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.server.MutableGameStageManager;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.PacketRegistry;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VContentRegistry;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.predicate.PredicateCompiler;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.predicate.PredicateData;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.CommonItemRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.CommonItemStackRestrictionEntryPacket;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NullMarked
public abstract class VItemAddon extends ItemAddon implements VAddon {
    private static @Nullable VItemAddon instance;

    private final ItemRecipeIntegration recipeIntegration = new ItemRecipeIntegration();

    public VItemAddon() {
        instance = this;
        DataDrivenCompiler.instance().register("predicate", PredicateData.class, new PredicateCompiler());
    }

    @Override
    public void onRegister(AddonManager<? extends Addon> addonManager) {
        recipeIntegration.register(addonManager);
    }

    @Override
    public void preReload(MutableGameStageManager instance) {
        VAddon.super.preReload(instance);
    }

    @Override
    public void postReload(MutableGameStageManager instance) {
        VAddon.super.postReload(instance);
    }

    @Override
    public void preCompileAll(RecompilationTask recompilationTask) {
        var factoryContextMap = new HashMap<ItemStackRestrictionResolverFactory<?>, Object>();
        for (var factory : ItemStackRestrictionResolverFactories.instance().getAll()) {
            factoryContextMap.put(factory, factory.createContext(recompilationTask));
        }
        recompilationTask.setContext(this, new ItemCompileContext(factoryContextMap, new CompilationContext()));

        super.preCompileAll(recompilationTask);
    }

    @Override
    public void postCompileAll(RecompilationTask recompilationTask) {
        var itemMap = new HashMap<Holder<Item>, CompiledRestrictionEntry>();
        var flattener = recompilationTask.instance().get(GameContentFlattener.Attribute.INSTANCE);
        for (var value : recompilationTask.stages().compiledRestrictionEntryMap().values()) {
            var items = flattener.flatten(value.gameContent(), CommonItemCollection.TYPE);
            for (var item : items.items()) {
                assert item != null;
                itemMap.put(item, value);
            }
        }
        recompilationTask.stages().addonData().put(this, new ItemAddonData(itemMap));
        var context = (ItemCompileContext) Objects.requireNonNull(recompilationTask.getContext(this));
    }

    @Override
    protected CustomPacket createPacket(ItemStackRestrictionEntryReference reference, ItemStackRestrictionEntry entry) {
        return new CommonItemStackRestrictionEntryPacket(reference, entry);
    }

    @Override
    public void registerCustomContent(ContentRegistry registry) {
        registry
                .prepare(CommonItemCollection.TYPE)
                .set(ContentRegistry.NAME, "item")
                .set(ContentRegistry.FLATTENER_FACTORY, new ItemFlattenerFactory())
                .set(VContentRegistry.GAME_CONTENT_SERIALIZER, CommonItemCollection.SERIALIZER)
                .register();
    }

    @Override
    public void registerPackets(PacketRegistry registry) {
        registry.playClientBound(CommonItemRestrictionPacket.TYPE, CommonItemRestrictionPacket.STREAM_CODEC);
        registry.playClientBound(CommonItemStackRestrictionEntryPacket.TYPE, CommonItemStackRestrictionEntryPacket.STREAM_CODEC);
    }

    public void handle(CommonItemRestrictionPacket packet) {
        var factory = ItemStackRestrictionResolverFactories.instance().getFactory(packet.dataDrivenData().factoryId());
        if (factory == null) throw new IllegalStateException("Unknown factory: " + packet.dataDrivenData().factoryId());
        var networkData = packet.dataDrivenData();
        var entry = new CommonItemRestrictionEntry(RestrictionEntryOrigin.string(packet.origin()), packet.targetCollection(), networkData);
        ClientGameStageManager.instance().addRestriction(entry);
    }

    public void handle(CommonItemStackRestrictionEntryPacket packet) {
        ClientGameStageManager
                .instance()
                .get(STAGE_MANAGER_CONTEXT)
                .addRestrictionEntry(packet.reference(), packet.entry());
    }

    @Override
    public void postReload(MutableGameStageManager gameStageManager, ReloadableServerResources serverResources, RegistryAccess registryAccess) {
        VAddon.super.postReload(gameStageManager, serverResources, registryAccess);
        var flattener = gameStageManager.get(GameContentFlattener.Attribute.INSTANCE);
        var index = gameStageManager.get(Index.ATTRIBUTE);
        for (var restriction : gameStageManager.restrictions()) {
            if (restriction instanceof CommonItemRestrictionEntry c) {
                var flattened = flattener.flatten(c.targetItems()).get(CommonItemCollection.TYPE);
                for (var item : flattened.items()) {
                    Objects.requireNonNull(item);
                    index.entryMap.put(item, c);
                }
            }
        }
    }

    public static @Nullable ItemStackRestrictionEntry getEntry(MutableGameStageManager instance, ItemStack nmsItemStack, de.dasbabypixel.gamestages.common.data.ItemStack ourItemStack) {
        var index = instance.get(Index.ATTRIBUTE);
        var entry = index.entryMap.get(nmsItemStack.getItemHolder());
        if (entry == null) return null;

        return null;
    }

    public static @Nullable CompiledItemStackRestrictionEntry getEntry(BaseStages stages, ItemStack nmsItemStack, de.dasbabypixel.gamestages.common.data.ItemStack ourItemStack) {
        var data = (ItemAddonData) stages.addonData().get(instance());
        if (data == null) return null;
        var entry = data.itemMap.get(nmsItemStack.getItemHolder());
        if (entry == null) return null;
        var resolver = ((CommonItemRestrictionEntry.Compiled) entry).resolver();
        return resolver.resolveRestrictionEntry(ourItemStack);
    }

    public static VItemAddon instance() {
        return Objects.requireNonNull(instance);
    }

    public static class Index {
        public static final Attribute<AbstractGameStageManager<?>, Index> ATTRIBUTE = new Attribute<>(Index::new);
        public final Map<Holder<Item>, CommonItemRestrictionEntry> entryMap = new HashMap<>();
    }

    public record ItemAddonData(Map<Holder<Item>, CompiledRestrictionEntry> itemMap) {
    }

    public record ItemCompileContext(Map<ItemStackRestrictionResolverFactory<?>, Object> factoryContextMap,
                                     CompilationContext compilationContext) implements RecompileContext {
    }
}
