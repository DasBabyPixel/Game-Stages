package de.dasbabypixel.gamestages.common.data.restriction.compiled;

import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class RestrictionEntryCompiler {
    private final @NonNull RecompilationTask recompilationTask;
    private final @NonNull RestrictionEntryPreCompiler preCompiler;

    public RestrictionEntryCompiler(@NonNull RecompilationTask recompilationTask) {
        this.recompilationTask = recompilationTask;
        this.preCompiler = recompilationTask.instance().get(RestrictionEntryPreCompiler.ATTRIBUTE);
    }

    @SuppressWarnings("unchecked")
    public @NonNull CompiledRestrictionEntry compile(@NonNull RestrictionEntry<?, ?> entry) {
        var preCompiled = Objects.requireNonNull(preCompiler.preCompiledCache().get(entry));
        return ((RestrictionEntry<?, Object>) entry).compile(recompilationTask, preCompiled);
    }
}
