package de.dasbabypixel.gamestages.common.addons.item;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryCompiler;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.attribute.Attribute;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NullMarked
public abstract class ItemAddon implements Addon {
    public static final Attribute<AbstractGameStageManager<?>, StageManagerContext> STAGE_MANAGER_CONTEXT = new Attribute<>(StageManagerContext::new);
    private static @Nullable ItemAddon instance;

    public ItemAddon() {
        instance = this;
        NETWORK_SYNC_CONFIG_EVENT.addListener(this::handle);
        COMPILE_ALL_PRE_EVENT.addListener(this::handle);
        RELOAD_POST_EVENT.addListener(this::handle);
        PRE_COMPILE_PREPARE_EVENT.addListener(this::handle);
    }

    private void handle(CompileAllPreEvent event) {
        var recompilationTask = event.recompilationTask();
        var context = recompilationTask.instance().get(STAGE_MANAGER_CONTEXT);
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
        for (var entry : instance.get(STAGE_MANAGER_CONTEXT).restrictionEntryMap.entrySet()) {
            assert entry != null;
            var packet = createPacket(entry.getKey(), entry.getValue());
            packetConsumer.send(packet);
        }
    }

    private void handle(PreCompilePrepareEvent event) {
        var manager = event.manager();
        var preCompileContext = manager.get(PreCompileContext.ATTRIBUTE);
        if (!preCompileContext.factoryContextMap.isEmpty()) throw new IllegalStateException();
        for (var factory : ItemStackRestrictionResolverFactories.instance().getAll()) {
            preCompileContext.factoryContextMap.put(factory, factory.createContext(manager));
        }
    }

    private void handle(ReloadPostEvent event) {
    }

    protected abstract CustomPacket createPacket(ItemStackRestrictionEntryReference reference, ItemStackRestrictionEntry entry);

    public static ItemAddon instance() {
        return Objects.requireNonNull(instance);
    }

    public static class CompilationContext {
        public static final Attribute<RecompilationTask, CompilationContext> ATTRIBUTE = new Attribute<>(CompilationContext::new);
        public final Map<ItemStackRestrictionEntryReference, CompiledItemStackRestrictionEntry> compiledMap = new HashMap<>();
    }

    public static class PreCompileContext {
        public static final Attribute<AbstractGameStageManager<?>, PreCompileContext> ATTRIBUTE = new Attribute<>(PreCompileContext::new);

        private final Map<ItemStackRestrictionResolverFactory<?>, Object> factoryContextMap = new HashMap<>();

        @SuppressWarnings("unchecked")
        public <C> C get(ItemStackRestrictionResolverFactory<C> factory) {
            return (C) Objects.requireNonNull(factoryContextMap.get(factory));
        }
    }

    public static class StageManagerContext {
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
