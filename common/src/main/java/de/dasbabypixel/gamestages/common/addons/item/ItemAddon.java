package de.dasbabypixel.gamestages.common.addons.item;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryCompiler;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.data.PlayerCompilationTask;
import de.dasbabypixel.gamestages.common.data.attribute.CompilableAttribute;
import de.dasbabypixel.gamestages.common.data.attribute.ImmutableAttribute;
import de.dasbabypixel.gamestages.common.data.attribute.SimpleAttribute;
import de.dasbabypixel.gamestages.common.data.attribute.SimpleImmutableAttribute;
import de.dasbabypixel.gamestages.common.data.manager.immutable.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.SimpleMutableGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.compiler.ManagerCompilerTask;
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
        COMPILE_ALL_POST_EVENT.addListener(this::handle);
        PRE_COMPILE_PREPARE_EVENT.addListener(this::handle);
        RELOAD_PRE_EVENT.addListener(this::handle);
    }

    private void handle(ReloadPreEvent event) {
        event.manager().init(MutableStageManagerContext.MUTABLE_MANAGER_ATTRIBUTE, new MutableStageManagerContext());
    }

    private void handle(CompileAllPreEvent event) {
        var recompilationTask = event.playerCompilationTask();
        var context = recompilationTask.manager().get(StageManagerContext.ATTRIBUTE);
        var compilationContext = recompilationTask.init(CompilationContext.ATTRIBUTE, new CompilationContext());
        for (var entry : context.restrictionEntryMap.entrySet()) {
            assert entry != null;
            var compiled = ItemStackRestrictionEntryCompiler.compile(recompilationTask.predicateCompiler(), entry.getValue());
            compilationContext.compiledMap.put(entry.getKey(), compiled);
        }
    }

    private void handle(CompileAllPostEvent event) {
    }

    private void handle(NetworkSyncConfigEvent event) {
        var manager = event.manager();
        var packetConsumer = event.packetConsumer();
        for (var entry : manager.get(StageManagerContext.ATTRIBUTE).restrictionEntryMap.entrySet()) {
            assert entry != null;
            var packet = createPacket(entry.getKey(), entry.getValue());
            packetConsumer.send(packet);
        }
    }

    private void handle(PreCompilePrepareEvent event) {
        var task = event.task();
        var preCompileContext = task.init(PreCompileContext.ATTRIBUTE, new PreCompileContext());
        task.init(StageManagerContext.TASK_ATTRIBUTE, new StageManagerContext(task.manager()
                .get(MutableStageManagerContext.MUTABLE_MANAGER_ATTRIBUTE).restrictionEntryMap));
        if (!preCompileContext.factoryContextMap.isEmpty()) throw new IllegalStateException();
        for (var factory : ItemStackRestrictionResolverFactories.instance().getAll()) {
            preCompileContext.factoryContextMap.put(factory, factory.createContext(task));
        }
    }

    protected abstract CustomPacket createPacket(ItemStackRestrictionEntryReference reference, ItemStackRestrictionEntry entry);

    public static ItemAddon instance() {
        return Objects.requireNonNull(instance);
    }

    public static class CompilationContext {
        public static final SimpleAttribute<PlayerCompilationTask, CompilationContext> ATTRIBUTE = new SimpleAttribute<>();
        public final Map<ItemStackRestrictionEntryReference, CompiledItemStackRestrictionEntry> compiledMap = new HashMap<>();
    }

    public static class PreCompileContext {
        public static final SimpleAttribute<ManagerCompilerTask, PreCompileContext> ATTRIBUTE = new SimpleAttribute<>();

        private final Map<ItemStackRestrictionResolverFactory<?>, Object> factoryContextMap = new HashMap<>();

        @SuppressWarnings("unchecked")
        public <C> C get(ItemStackRestrictionResolverFactory<C> factory) {
            return (C) Objects.requireNonNull(factoryContextMap.get(factory));
        }
    }

    public static final class StageManagerContext {
        public static final SimpleAttribute<ManagerCompilerTask, StageManagerContext> TASK_ATTRIBUTE = new SimpleAttribute<>();
        public static final ImmutableAttribute<AbstractGameStageManager<?>, StageManagerContext> ATTRIBUTE = new SimpleImmutableAttribute<>();
        private final Map<ItemStackRestrictionEntryReference, ItemStackRestrictionEntry> restrictionEntryMap;

        public StageManagerContext(Map<ItemStackRestrictionEntryReference, ItemStackRestrictionEntry> restrictionEntryMap) {
            this.restrictionEntryMap = Objects.requireNonNull(Map.copyOf(restrictionEntryMap));
        }

        public ItemStackRestrictionEntry getEntry(ItemStackRestrictionEntryReference reference) {
            return Objects.requireNonNull(restrictionEntryMap.get(reference));
        }
    }

    public static class MutableStageManagerContext {
        public static final CompilableAttribute<SimpleMutableGameStageManager<?, ?>, MutableStageManagerContext, AbstractGameStageManager<?>> MUTABLE_MANAGER_ATTRIBUTE = (builder, value) -> {
            var task = builder.compiler().get(ManagerCompilerTask.ATTRIBUTE);
            builder.add(StageManagerContext.ATTRIBUTE, task.get(StageManagerContext.TASK_ATTRIBUTE));
        };
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
