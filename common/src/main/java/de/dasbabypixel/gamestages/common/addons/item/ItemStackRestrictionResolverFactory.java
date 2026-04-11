package de.dasbabypixel.gamestages.common.addons.item;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.compilation.CompilableResource;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class ItemStackRestrictionResolverFactory<CompilationContext> {
    private final String factoryId;

    public ItemStackRestrictionResolverFactory(String factoryId) {
        this.factoryId = factoryId;
    }

    public String factoryId() {
        return factoryId;
    }

    public PreCompiled precompile(DataDrivenTypedData<?> data, CompilationContext context) {
        return precompileInternal(data, context);
    }

    public abstract CompilationContext createContext(AbstractGameStageManager<?> instance);

    protected abstract PreCompiled precompileInternal(DataDrivenTypedData<?> data, CompilationContext context);

    public interface PreCompiled extends CompilableResource.PreCompiled<RecompilationTask, ItemStackRestrictionResolver> {
        @Override
        ItemStackRestrictionResolver compile(RecompilationTask task);
    }
}
