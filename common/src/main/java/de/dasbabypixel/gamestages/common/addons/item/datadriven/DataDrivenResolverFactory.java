package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolver;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactory;
import de.dasbabypixel.gamestages.common.data.ItemStack;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class DataDrivenResolverFactory extends ItemStackRestrictionResolverFactory<DataDrivenResolverFactory.Context> {
    public DataDrivenResolverFactory() {
        super("data_driven");
    }

    @Override
    public Context createContext(RecompilationTask task) {
        return new Context(task);
    }

    @Override
    protected ItemStackRestrictionResolver compileInternal(DataDrivenTypedData<?> data, Context context) {
        return new ItemStackRestrictionResolver() {
            private final CompiledResolverAlgorithm algorithm = DataDrivenCompiler.instance().compile(data, context);

            @Override
            public @Nullable CompiledItemStackRestrictionEntry resolveRestrictionEntry(ItemStack itemStack) {
                return algorithm.resolve(itemStack);
            }

            @Override
            public List<CompiledItemStackRestrictionEntry> restrictionEntries() {
                return algorithm.entries();
            }
        };
    }

    public record Context(RecompilationTask task) {
    }
}
