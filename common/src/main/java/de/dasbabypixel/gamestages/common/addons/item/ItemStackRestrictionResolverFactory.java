package de.dasbabypixel.gamestages.common.addons.item;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
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

    public ItemStackRestrictionResolver compile(DataDrivenTypedData<?> data, CompilationContext context) {
        return compileInternal(data, context);
    }

    public abstract CompilationContext createContext(RecompilationTask task);

    protected abstract ItemStackRestrictionResolver compileInternal(DataDrivenTypedData<?> data, CompilationContext context);
}
