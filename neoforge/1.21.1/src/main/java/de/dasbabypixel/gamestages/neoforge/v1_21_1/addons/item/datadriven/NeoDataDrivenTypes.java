package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item.datadriven;

import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolver;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactories;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactory;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.CompiledItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryCompiler;
import de.dasbabypixel.gamestages.common.data.ItemStack;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.VDataDrivenTypes;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenTypes;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@NullMarked
public class NeoDataDrivenTypes {
    public static void register(DataDrivenTypes types, ItemStackRestrictionResolverFactories factories) {
        VDataDrivenTypes.register(types, factories);
        factories.register(new BuiltinItemFactory());
    }

    private static class BuiltinItemFactory extends ItemStackRestrictionResolverFactory<BuiltinItemFactory.Context> {
        public BuiltinItemFactory() {
            super("builtin_item");
        }

        @Override
        public Context createContext(RecompilationTask task) {
            return new Context(task);
        }

        @Override
        protected ItemStackRestrictionResolver compileInternal(DataDrivenTypedData<?> data, Context context) {
            var entry = (ItemStackRestrictionEntry) Objects.requireNonNull(data.data());
            var compiled = ItemStackRestrictionEntryCompiler.compile(context.task.predicateCompiler(), entry);
            return new ItemStackRestrictionResolver() {
                @Override
                public CompiledItemStackRestrictionEntry resolveRestrictionEntry(ItemStack itemStack) {
                    return compiled;
                }

                @Override
                public List<CompiledItemStackRestrictionEntry> restrictionEntries() {
                    return List.of(compiled);
                }
            };
        }

        private static class Context {
            private final RecompilationTask task;

            public Context(RecompilationTask task) {
                this.task = task;
            }
        }
    }
}
