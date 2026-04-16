package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolver;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactory;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
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
    public Context createContext(AbstractGameStageManager<?> instance) {
        return new Context(instance);
    }

    @Override
    protected PreCompiled<?> precompileInternal(DataDrivenTypedData<?> data, Context context) {
        var pc = data.data().precompile(context.instance());
        return new PreCompiled<>(pc, pc.entries());
    }

    public record PreCompiled<Compiled extends DataDrivenData.Compiled<?>>(
            DataDrivenData.PreCompiled<?, Compiled> preCompiled,
            List<ItemStackRestrictionEntry> entries) implements ItemStackRestrictionResolverFactory.PreCompiled {
        public PreCompiled {
            entries = List.copyOf(entries);
        }

        @Override
        public @Nullable ItemStackRestrictionEntry resolve(ItemStack itemStack) {
            return preCompiled.resolve(itemStack);
        }

        @Override
        public ItemStackRestrictionResolver compile(RecompilationTask task) {
            return new ItemStackRestrictionResolver() {
                private final ResolverAlgorithmData<?, CompiledItemStackRestrictionEntry> data = preCompiled.compile(task);

                @Override
                public @Nullable CompiledItemStackRestrictionEntry resolveRestrictionEntry(ItemStack itemStack) {
                    return data.resolve(itemStack);
                }
            };
        }
    }

    public record Context(AbstractGameStageManager<?> instance) {
    }
}
