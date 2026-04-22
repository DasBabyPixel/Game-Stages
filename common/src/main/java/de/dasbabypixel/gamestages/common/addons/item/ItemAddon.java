package de.dasbabypixel.gamestages.common.addons.item;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryCompiler;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.data.PlayerCompilationTask;
import de.dasbabypixel.gamestages.common.data.attribute.Attribute;
import de.dasbabypixel.gamestages.common.data.attribute.AttributeQuery;
import de.dasbabypixel.gamestages.common.data.manager.immutable.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.AbstractMutableGameStageManager;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NullMarked
public abstract class ItemAddon implements Addon {
    private static @Nullable ItemAddon instance;

    public ItemAddon() {
        instance = this;
        NETWORK_SYNC_CONFIG_EVENT.addListener(this::handle);
        COMPILE_ALL_PRE_EVENT.addListener(this::handle);
        PRE_COMPILE_PREPARE_EVENT.addListener(this::handle);
        COMPILE_MANAGER_EVENT.addListener(this::handle);
    }

    private void handle(CompileManagerEvent event) {
        var ctx = event.task().manager().get(MutableStageManagerContext.ATTRIBUTE);
        StageManagerContext.ATTRIBUTE.init(event.immutableManager(), new StageManagerContext(ctx.restrictionEntryMap));
    }

    private void handle(CompileAllPreEvent event) {
        var recompilationTask = event.playerCompilationTask();
        var context = recompilationTask.manager().get(StageManagerContext.ATTRIBUTE);
        var compilationContext = recompilationTask.get(CompilationContext.ATTRIBUTE);
        for (var entry : context.restrictionEntryMap.entrySet()) {
            assert entry != null;
            var compiled = ItemStackRestrictionEntryCompiler.compile(recompilationTask.predicateCompiler(), entry.getValue());
            compilationContext.compiledMap.put(entry.getKey(), compiled);
        }
    }

    private void handle(NetworkSyncConfigEvent event) {
        var instance = event.manager();
        var packetConsumer = event.packetConsumer();
        for (var entry : instance.get(StageManagerContext.ATTRIBUTE).restrictionEntryMap.entrySet()) {
            assert entry != null;
            var packet = createPacket(entry.getKey(), entry.getValue());
            packetConsumer.send(packet);
        }
    }

    private void handle(PreCompilePrepareEvent event) {
        var task = event.task();
        var manager = task.manager();
        var preCompileContext = manager.get(PreCompileContext.ATTRIBUTE);
        if (!preCompileContext.factoryContextMap.isEmpty()) throw new IllegalStateException();
        for (var factory : ItemStackRestrictionResolverFactories.instance().getAll()) {
            preCompileContext.factoryContextMap.put(factory, factory.createContext(manager));
        }
    }

    protected abstract CustomPacket createPacket(ItemStackRestrictionEntryReference reference, ItemStackRestrictionEntry entry);

    public static ItemAddon instance() {
        return Objects.requireNonNull(instance);
    }

    public static class CompilationContext {
        public static final Attribute<PlayerCompilationTask, CompilationContext> ATTRIBUTE = new Attribute<>(CompilationContext::new);
        public final Map<ItemStackRestrictionEntryReference, CompiledItemStackRestrictionEntry> compiledMap = new HashMap<>();
    }

    public static class PreCompileContext {
        public static final Attribute<AbstractMutableGameStageManager<?>, PreCompileContext> ATTRIBUTE = new Attribute<>(PreCompileContext::new);

        private final Map<ItemStackRestrictionResolverFactory<?>, Object> factoryContextMap = new HashMap<>();

        @SuppressWarnings("unchecked")
        public <C> C get(ItemStackRestrictionResolverFactory<C> factory) {
            return (C) Objects.requireNonNull(factoryContextMap.get(factory));
        }
    }

    public static final class StageManagerContext {
        public static final AttributeQuery.Holder<AbstractGameStageManager<?>, StageManagerContext> ATTRIBUTE = AttributeQuery.holder();
        private final Map<ItemStackRestrictionEntryReference, ItemStackRestrictionEntry> restrictionEntryMap;

        public StageManagerContext(Map<ItemStackRestrictionEntryReference, ItemStackRestrictionEntry> restrictionEntryMap) {
            this.restrictionEntryMap = Objects.requireNonNull(Map.copyOf(restrictionEntryMap));
        }
    }

    public static class MutableStageManagerContext {
        public static final Attribute<AbstractMutableGameStageManager<?>, MutableStageManagerContext> ATTRIBUTE = new Attribute<>(MutableStageManagerContext::new);
        private final Map<ItemStackRestrictionEntryReference, ItemStackRestrictionEntry> restrictionEntryMap = new HashMap<>();
        private int idCounter = 0;

        public void addRestrictionEntry(ItemStackRestrictionEntryReference reference, ItemStackRestrictionEntry restrictionEntry) {
            if (restrictionEntryMap.containsKey(reference))
                throw new IllegalStateException("Duplicate reference: " + reference);
            restrictionEntryMap.put(reference, restrictionEntry);
        }

        public ItemStackRestrictionEntryReference addRestrictionEntry(ItemStackRestrictionEntry restrictionEntry) {
            var reference = new ItemStackRestrictionEntryReference("ref_" + idCounter++);
            addRestrictionEntry(reference, restrictionEntry);
            return reference;
        }

        public ItemStackRestrictionEntry getEntry(ItemStackRestrictionEntryReference reference) {
            return Objects.requireNonNull(restrictionEntryMap.get(reference));
        }
    }
}
