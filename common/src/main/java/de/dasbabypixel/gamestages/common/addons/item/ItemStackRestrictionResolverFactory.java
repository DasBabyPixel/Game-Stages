package de.dasbabypixel.gamestages.common.addons.item;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.ItemStack;
import de.dasbabypixel.gamestages.common.data.PlayerCompilationTask;
import de.dasbabypixel.gamestages.common.data.compilation.CompilableResource;
import de.dasbabypixel.gamestages.common.data.manager.mutable.AbstractMutableGameStageManager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

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

    public abstract CompilationContext createContext(AbstractMutableGameStageManager<?> instance);

    protected abstract PreCompiled precompileInternal(DataDrivenTypedData<?> data, CompilationContext context);

    public interface PreCompiled extends CompilableResource<PlayerCompilationTask, ItemStackRestrictionResolver> {
        @Nullable ItemStackRestrictionEntry resolve(ItemStack itemStack);

        List<ItemStackRestrictionEntry> entries();

        @Override
        ItemStackRestrictionResolver compile(PlayerCompilationTask task);
    }
}
