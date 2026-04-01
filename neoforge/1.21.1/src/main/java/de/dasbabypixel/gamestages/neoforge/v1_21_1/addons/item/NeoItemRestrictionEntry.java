package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolver;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactories;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactory;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionEntryPreCompiler;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.CommonItemCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.CommonItemRestrictionEntry;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.CommonItemRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenNetwork;
import org.jspecify.annotations.NonNull;

public class NeoItemRestrictionEntry extends CommonItemRestrictionEntry<NeoItemRestrictionEntry, NeoItemRestrictionEntry.PreCompiled> {
    public NeoItemRestrictionEntry(@NonNull RestrictionEntryOrigin origin, @NonNull GameContent targetItems, DataDrivenNetwork.@NonNull NetworkData<?> dataDrivenNetworkData) {
        super(origin, targetItems, dataDrivenNetworkData);
    }

    @Override
    public @NonNull CustomPacket createPacket(@NonNull ServerGameStageManager instance) {
        var items = instance
                .get(GameContentFlattener.Attribute.INSTANCE)
                .flatten(targetItems(), CommonItemCollection.TYPE);
        return new CommonItemRestrictionPacket(items, origin().toString(), dataDrivenNetworkData());
    }

    @Override
    public NeoItemRestrictionEntry.@NonNull PreCompiled precompile(@NonNull AbstractGameStageManager instance, @NonNull RestrictionEntryPreCompiler preCompiler) {
        var items = instance
                .get(GameContentFlattener.Attribute.INSTANCE)
                .flatten(targetItems(), CommonItemCollection.TYPE);
        return new PreCompiled(items);
    }

    @Override
    public @NonNull CompiledRestrictionEntry compile(@NonNull RecompilationTask task, NeoItemRestrictionEntry.@NonNull PreCompiled preCompiled) {
        var networkData = dataDrivenNetworkData();
        var factoryId = networkData.factoryId();
        var factory = ItemStackRestrictionResolverFactories.instance().getFactory(factoryId);
        if (factory == null) throw new IllegalStateException("Unknown factory " + factoryId);
        var resolver = compile(factory, task, new DataDrivenTypedData<DataDrivenData>(networkData
                .type()
                .type(), networkData.data()));
        return new Compiled(this, preCompiled.items, resolver);
    }

    private <T> ItemStackRestrictionResolver compile(@NonNull ItemStackRestrictionResolverFactory<T> factory, @NonNull RecompilationTask task, @NonNull DataDrivenTypedData<?> data) {
        var ctx = factory.createContext(task);
        return factory.compile(data, ctx);
    }

    public record Compiled(@NonNull NeoItemRestrictionEntry entry, @NonNull CommonItemCollection gameContent,
                           @NonNull ItemStackRestrictionResolver resolver) implements CompiledRestrictionEntry {
        @Override
        public @NonNull RestrictionEntryOrigin origin() {
            return entry.origin();
        }
    }

    public record PreCompiled(@NonNull CommonItemCollection items) {
    }
}
