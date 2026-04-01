package de.dasbabypixel.gamestages.common.addons.item;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import org.jspecify.annotations.NonNull;

public abstract class ItemStackRestrictionResolverFactory<CompilationContext> {
    private final String factoryId;

    public ItemStackRestrictionResolverFactory(@NonNull String factoryId) {
        this.factoryId = factoryId;
    }

    public @NonNull String factoryId() {
        return factoryId;
    }

    public @NonNull ItemStackRestrictionResolver compile(@NonNull DataDrivenTypedData<?> data, CompilationContext context) {
        return compileInternal(data, context);
    }

    public abstract CompilationContext createContext(@NonNull RecompilationTask task);

    protected abstract @NonNull ItemStackRestrictionResolver compileInternal(@NonNull DataDrivenTypedData<?> data, CompilationContext context);
}
