package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolver;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactory;
import de.dasbabypixel.gamestages.common.data.ItemStack;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class DataDrivenResolverFactory extends ItemStackRestrictionResolverFactory<DataDrivenResolverFactory.Context> {
    public DataDrivenResolverFactory() {
        super("data_driven");
    }

    @Override
    public @NonNull Context createContext(@NonNull RecompilationTask task) {
        return new Context();
    }

    @Override
    protected @NonNull ItemStackRestrictionResolver compileInternal(@NonNull DataDrivenTypedData<?> data, Context context) {
        return new ItemStackRestrictionResolver() {
            private final CompiledResolverAlgorithm algorithm = DataDrivenCompiler.instance().compile(data);

            @Override
            public @Nullable CompiledItemStackRestrictionEntry resolveRestrictionEntry(@NonNull ItemStack itemStack) {
                return algorithm.resolve(itemStack);
            }

            @Override
            public @NonNull List<@NonNull CompiledItemStackRestrictionEntry> restrictionEntries() {
                return algorithm.entries();
            }
        };
    }

    public static class Context {
    }
}
