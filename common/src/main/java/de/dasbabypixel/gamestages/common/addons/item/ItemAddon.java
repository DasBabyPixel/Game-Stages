package de.dasbabypixel.gamestages.common.addons.item;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryCompiler;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.attribute.Attribute;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.network.PacketConsumer;
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
    }

    @Override
    public void preCompileAll(RecompilationTask recompilationTask) {
        var context = recompilationTask.instance().get(STAGE_MANAGER_CONTEXT);
        var compilationContext = ((RecompileContext) recompilationTask.getContext(this)).compilationContext();
        System.out.println(context.restrictionEntryMap);
        for (var entry : context.restrictionEntryMap.entrySet()) {
            assert entry != null;
            var compiled = ItemStackRestrictionEntryCompiler.compile(recompilationTask.predicateCompiler(), entry.getValue());
            System.out.println("prepare " + entry.getKey());
            compilationContext.compiledMap.put(entry.getKey(), compiled);
        }
    }

    @Override
    public void onSyncConfigToPlayer(ServerGameStageManager instance, PacketConsumer packetConsumer) {
        Addon.super.onSyncConfigToPlayer(instance, packetConsumer);
        for (var entry : instance.get(STAGE_MANAGER_CONTEXT).restrictionEntryMap.entrySet()) {
            assert entry != null;
            var packet = createPacket(entry.getKey(), entry.getValue());
            packetConsumer.send(packet);
        }
    }

    protected abstract CustomPacket createPacket(ItemStackRestrictionEntryReference reference, ItemStackRestrictionEntry entry);

    public static ItemAddon instance() {
        return Objects.requireNonNull(instance);
    }

    public interface RecompileContext {
        CompilationContext compilationContext();
    }

    public static class CompilationContext {
        public final Map<ItemStackRestrictionEntryReference, CompiledItemStackRestrictionEntry> compiledMap = new HashMap<>();
    }

    public static class StageManagerContext {
        private final Map<ItemStackRestrictionEntryReference, ItemStackRestrictionEntry> restrictionEntryMap = new HashMap<>();

        public void addRestrictionEntry(ItemStackRestrictionEntryReference reference, ItemStackRestrictionEntry restrictionEntry) {
            if (restrictionEntryMap.containsKey(reference))
                throw new IllegalStateException("Duplicate reference: " + reference);
            restrictionEntryMap.put(reference, restrictionEntry);
        }

        public ItemStackRestrictionEntry getEntry(ItemStackRestrictionEntryReference reference) {
            return Objects.requireNonNull(restrictionEntryMap.get(reference));
        }
    }
}
