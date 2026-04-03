package de.dasbabypixel.gamestages.common.addons.item;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryCompiler;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ItemAddon implements Addon {
    public static final AbstractGameStageManager.Attribute<StageManagerContext> STAGE_MANAGER_CONTEXT = new AbstractGameStageManager.Attribute<>(StageManagerContext::new);
    private static ItemAddon instance;

    public ItemAddon() {
        instance = this;
    }

    @Override
    public void preCompileAll(@NonNull RecompilationTask recompilationTask) {
        var context = recompilationTask.instance().get(STAGE_MANAGER_CONTEXT);
        var compilationContext = ((RecompileContext) recompilationTask.getContext(this)).compilationContext();
        for (var entry : context.restrictionEntryMap.entrySet()) {
            assert entry != null;
            var compiled = ItemStackRestrictionEntryCompiler.compile(recompilationTask.predicateCompiler(), entry.getValue());
            compilationContext.compiledMap.put(entry.getKey(), compiled);
        }
    }

    public static @NonNull ItemAddon instance() {
        return Objects.requireNonNull(instance);
    }

    public interface RecompileContext {
        @NonNull CompilationContext compilationContext();
    }

    public static class CompilationContext {
        public final @NonNull Map<@NonNull ItemStackRestrictionEntryReference, @NonNull CompiledItemStackRestrictionEntry> compiledMap = new HashMap<>();
    }

    public static class StageManagerContext {
        private final @NonNull Map<@NonNull ItemStackRestrictionEntryReference, @NonNull ItemStackRestrictionEntry> restrictionEntryMap = new HashMap<>();

        public void addRestrictionEntry(@NonNull ItemStackRestrictionEntryReference reference, @NonNull ItemStackRestrictionEntry restrictionEntry) {
            if (restrictionEntryMap.containsKey(reference))
                throw new IllegalStateException("Duplicate reference: " + reference);
            restrictionEntryMap.put(reference, restrictionEntry);
        }

        public @NonNull ItemStackRestrictionEntry getEntry(@NonNull ItemStackRestrictionEntryReference reference) {
            return Objects.requireNonNull(restrictionEntryMap.get(reference));
        }
    }
}
