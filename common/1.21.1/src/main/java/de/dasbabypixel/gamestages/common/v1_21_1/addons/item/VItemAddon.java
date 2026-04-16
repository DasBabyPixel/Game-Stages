package de.dasbabypixel.gamestages.common.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.addon.AddonManager;
import de.dasbabypixel.gamestages.common.addon.ContentRegistry;
import de.dasbabypixel.gamestages.common.addons.item.ItemAddon;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactories;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.client.ClientGameStageManager;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.attribute.Attribute;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.restriction.predicates.Or;
import de.dasbabypixel.gamestages.common.data.server.MutableGameStageManager;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VAddon;
import de.dasbabypixel.gamestages.common.v1_21_1.addon.VContentRegistry;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.CommonItemRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.CommonItemStackRestrictionEntryPacket;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NullMarked
public abstract class VItemAddon extends ItemAddon implements VAddon {
    private static @Nullable VItemAddon instance;

    private final ItemRecipeIntegration recipeIntegration = new ItemRecipeIntegration();

    public VItemAddon() {
        instance = this;
        REGISTER_CUSTOM_CONTENT_EVENT.addListener(this::handle);
        COMPILE_ALL_POST_EVENT.addListener(this::handle);
        RELOAD_POST_EVENT.addListener(this::handle);
        REGISTER_PACKETS_EVENT.addListener(this::handle);
        SERVER_BUILD_DEPENDENCY_GRAPH_EVENT.addListener(this::handle);
        PRE_COMPILE_PREPARE_EVENT.addListener(this::handle);
    }

    private void handle(ServerBuildDependencyGraphEvent event) {
        var graph = event.dependencyGraph();
        for (var restriction : event.manager().restrictions()) {
            if (restriction instanceof CommonItemRestrictionEntry item) {
                var resolver = item.precompileItemStackResolver(event.manager());
                var resolverEntries = new ArrayList<PreparedRestrictionPredicate>();
                for (var entry : resolver.entries()) {
                    var predicate = entry.predicate();
                    resolverEntries.add(predicate);
                }
                var resolverConditionForItem = Or.INSTANCE.prepare(resolverEntries);

            }
        }
    }

    @Override
    public void onRegister(AddonManager<? extends Addon> addonManager) {
        recipeIntegration.register(addonManager);
    }

    private void handle(PreCompilePrepareEvent event) {
        var instance = event.manager();
        var index = instance.get(PreCompileItemIndex.ATTRIBUTE);
        var preCompileIndex = instance.get(AbstractGameStageManager.PreCompileIndex.ATTRIBUTE);
        var typeIndex = preCompileIndex.typeIndex(CommonItemCollection.TYPE);
        for (var restriction : typeIndex.<CommonItemRestrictionEntry.PreCompiled>entries()) {
            var gameContent = restriction.gameContent();
            for (var item : gameContent.content()) {
                index.entryMap.put(item, restriction);
            }
        }
    }

    private void handle(ReloadPostEvent event) {
    }

    private void handle(CompileAllPostEvent event) {
        var recompilationTask = event.recompilationTask();
        var itemMap = new HashMap<Holder<Item>, CommonItemRestrictionEntry.Compiled>();
        var flattener = recompilationTask.instance().get(GameContentFlattener.Attribute.INSTANCE);
        var compileIndex = recompilationTask.stages().get(BaseStages.CompileIndex.ATTRIBUTE);
        for (var value : compileIndex.compiledRestrictionEntries()) {
            var items = flattener.flatten(value.gameContent(), CommonItemCollection.TYPE);
            for (var item : items.content()) {
                itemMap.put(item, (CommonItemRestrictionEntry.Compiled) value);
            }
        }

        var stages = recompilationTask.stages();
        stages.get(ItemAddonDataHolder.ATTRIBUTE).itemAddonData = new ItemAddonData(itemMap);
    }

    @Override
    protected CustomPacket createPacket(ItemStackRestrictionEntryReference reference, ItemStackRestrictionEntry entry) {
        return new CommonItemStackRestrictionEntryPacket(reference, entry);
    }

    private void handle(RegisterCustomContentEvent event) {
        event.contentRegistry()
                .prepare(CommonItemCollection.TYPE)
                .set(ContentRegistry.NAME, "item")
                .set(ContentRegistry.FLATTENER_FACTORY, new ItemFlattenerFactory())
                .set(VContentRegistry.GAME_CONTENT_SERIALIZER, CommonItemCollection.SERIALIZER)
                .register();
    }

    private void handle(RegisterPacketsEvent event) {
        var registry = event.registry();
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
        ClientGameStageManager.instance()
                .get(STAGE_MANAGER_CONTEXT)
                .addRestrictionEntry(packet.reference(), packet.entry());
    }

    public static @Nullable ItemStackRestrictionEntry getEntry(MutableGameStageManager instance, ItemStack nmsItemStack, de.dasbabypixel.gamestages.common.data.ItemStack ourItemStack) {
        var index = instance.get(PreCompileItemIndex.ATTRIBUTE);
        var entry = index.entryMap.get(nmsItemStack.getItemHolder());
        if (entry == null) return null;
        return entry.preCompiledItemStack().resolve(ourItemStack);
    }

    public static @Nullable CompiledItemStackRestrictionEntry getEntry(BaseStages stages, ItemStack nmsItemStack, de.dasbabypixel.gamestages.common.data.ItemStack ourItemStack) {
        var data = stages.get(ItemAddonDataHolder.ATTRIBUTE).itemAddonData();
        var entry = data.itemMap.get(nmsItemStack.getItemHolder());
        if (entry == null) return null;
        return entry.resolver().resolveRestrictionEntry(ourItemStack);
    }

    public static VItemAddon instance() {
        return Objects.requireNonNull(instance);
    }

    public static class PreCompileItemIndex {
        public static final Attribute<AbstractGameStageManager<?>, PreCompileItemIndex> ATTRIBUTE = new Attribute<>(PreCompileItemIndex::new);
        public final Map<Holder<Item>, CommonItemRestrictionEntry.PreCompiled> entryMap = new HashMap<>();
    }

    public static class ItemAddonDataHolder {
        public static final Attribute<BaseStages, ItemAddonDataHolder> ATTRIBUTE = new Attribute<>(ItemAddonDataHolder::new);
        private @Nullable ItemAddonData itemAddonData;

        public ItemAddonData itemAddonData() {
            return Objects.requireNonNull(itemAddonData);
        }
    }

    public record ItemAddonData(Map<Holder<Item>, CommonItemRestrictionEntry.Compiled> itemMap) {
        public ItemAddonData {
            itemMap = Map.copyOf(itemMap);
        }
    }
}
