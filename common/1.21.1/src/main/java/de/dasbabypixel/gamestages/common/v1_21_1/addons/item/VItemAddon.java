package de.dasbabypixel.gamestages.common.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.addon.AddonManager;
import de.dasbabypixel.gamestages.common.addon.ContentRegistry;
import de.dasbabypixel.gamestages.common.addons.item.ItemAddon;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactories;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.attribute.Attribute;
import de.dasbabypixel.gamestages.common.data.attribute.AttributeQuery;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.ClientMutableGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.compiler.ManagerCompilerTask;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
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
        REGISTER_PACKETS_EVENT.addListener(this::handle);
        COMPILE_MANAGER_EVENT.addListener(this::handle);
        POST_COMPILE_TYPE_EVENT.addListener(this::handle);
    }

    @Override
    public void onRegister(AddonManager<? extends Addon> addonManager) {
        recipeIntegration.register(addonManager);
    }

    private void handle(PostCompileTypeEvent event) {
        if (event.type() != CommonItemCollection.TYPE) return;
        var task = event.task();
        var index = task.get(MutablePreCompileItemIndex.ATTRIBUTE);
        var preCompileIndex = task.preCompileIndex();
        var typeIndex = preCompileIndex.typeIndex(CommonItemCollection.TYPE);
        for (var restriction : typeIndex.<CommonItemRestrictionEntry.PreCompiled>entries()) {
            var gameContent = restriction.gameContent();
            for (var item : gameContent.content()) {
                index.entryMap.put(item, restriction);
            }
        }
    }

    private void handle(CompileManagerEvent event) {
        if (event.immutableManager() instanceof ServerGameStageManager manager) {
            var entryMap = event.task().get(MutablePreCompileItemIndex.ATTRIBUTE).entryMap;
            var index = new PreCompileItemIndex(entryMap);
            PreCompileItemIndex.ATTRIBUTE.init(manager, index);
        }
    }

    private void handle(CompileAllPostEvent event) {
        var recompilationTask = event.playerCompilationTask();
        var itemMap = new HashMap<Holder<Item>, CommonItemRestrictionEntry.Compiled>();
        var compileIndex = recompilationTask.stages().get(BaseStages.CompileIndex.ATTRIBUTE);
        var typeIndex = compileIndex.typeIndex(CommonItemCollection.TYPE);
        for (var entry_ : typeIndex.entryByContent().values()) {
            var entry = (CommonItemRestrictionEntry.Compiled) entry_;
            for (var item : entry.gameContent().content()) {
                itemMap.put(item, entry);
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
        ClientMutableGameStageManager.buildingInstance().addRestriction(entry);
    }

    public void handle(CommonItemStackRestrictionEntryPacket packet) {
        ClientMutableGameStageManager.buildingInstance()
                .get(MutableStageManagerContext.ATTRIBUTE)
                .addRestrictionEntry(packet.reference(), packet.entry());
    }

    public static @Nullable ItemStackRestrictionEntry getEntry(ManagerCompilerTask manager, ItemStack nmsItemStack, de.dasbabypixel.gamestages.common.data.ItemStack ourItemStack) {
        var index = manager.get(MutablePreCompileItemIndex.ATTRIBUTE);
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
        public static final AttributeQuery.Holder<ServerGameStageManager, PreCompileItemIndex> ATTRIBUTE = AttributeQuery.holder();
        public final Map<Holder<Item>, CommonItemRestrictionEntry.PreCompiled> entryMap;

        public PreCompileItemIndex(Map<Holder<Item>, CommonItemRestrictionEntry.PreCompiled> entryMap) {
            this.entryMap = Objects.requireNonNull(Map.copyOf(entryMap));
        }
    }

    public static class MutablePreCompileItemIndex {
        public static final AttributeQuery<ManagerCompilerTask, MutablePreCompileItemIndex> ATTRIBUTE = new Attribute<>(MutablePreCompileItemIndex::new);
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
            itemMap = Objects.requireNonNull(Map.copyOf(itemMap));
        }
    }
}
