package de.dasbabypixel.gamestages.common.data.restriction.compiled;

import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class RestrictionEntryCompiler {
    private final RecompilationTask recompilationTask;
    private final RestrictionEntryPreCompiler preCompiler;

    public RestrictionEntryCompiler(RecompilationTask recompilationTask) {
        this.recompilationTask = recompilationTask;
        this.preCompiler = recompilationTask.instance().get(RestrictionEntryPreCompiler.ATTRIBUTE);
    }

    @SuppressWarnings("unchecked")
    public CompiledRestrictionEntry compile(RestrictionEntry<?, ?> entry) {
        var preCompiled = Objects.requireNonNull(preCompiler.preCompiledCache().get(entry));
        return ((RestrictionEntry<?, @NonNull Object>) entry).compile(recompilationTask, preCompiled);
    }
}
