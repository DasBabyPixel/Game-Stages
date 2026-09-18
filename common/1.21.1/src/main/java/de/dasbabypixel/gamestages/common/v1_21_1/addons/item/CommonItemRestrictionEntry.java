package de.dasbabypixel.gamestages.common.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.addons.item.AbstractItemRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.ItemAddon;
import de.dasbabypixel.gamestages.common.addons.item.ItemCollection;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolver;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactories;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactory;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import de.dasbabypixel.gamestages.common.data.PlayerCompilationTask;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.compiler.ManagerCompilerTask;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.CommonItemRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenNetwork;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CommonItemRestrictionEntry extends AbstractItemRestrictionEntry<CommonItemRestrictionEntry, CommonItemRestrictionEntry.PreCompiled, CommonItemRestrictionEntry.Compiled> {
    private final DataDrivenNetwork.NetworkData<?> dataDrivenNetworkData;

    public CommonItemRestrictionEntry(RestrictionEntryOrigin origin, ItemCollection targetItems, DataDrivenNetwork.NetworkData<?> dataDrivenNetworkData) {
        super(origin, targetItems);
        this.dataDrivenNetworkData = dataDrivenNetworkData;
    }

    public DataDrivenNetwork.NetworkData<?> dataDrivenNetworkData() {
        return dataDrivenNetworkData;
    }

    @Override
    public PreCompiled compile(ManagerCompilerTask task) {
        var items = (CommonItemCollection) targetItems();
        var preCompiledItemStackResolver = precompileItemStackResolver(task);
        return new PreCompiled(items, preCompiledItemStackResolver, origin(), dataDrivenNetworkData());
    }

    public ItemStackRestrictionResolverFactory.PreCompiled precompileItemStackResolver(ManagerCompilerTask task) {
        var networkData = dataDrivenNetworkData();
        var factoryId = networkData.factoryId();
        var factory = ItemStackRestrictionResolverFactories.instance().getFactory(factoryId);
        if (factory == null) throw new IllegalStateException("Unknown factory " + factoryId);
        return precompile(task, factory, networkData.data().toTypedData());
    }

    private <T> ItemStackRestrictionResolverFactory.PreCompiled precompile(ManagerCompilerTask task, ItemStackRestrictionResolverFactory<T> factory, DataDrivenTypedData<?> data) {
        var context = task.get(ItemAddon.PreCompileContext.ATTRIBUTE).get(factory);
        return factory.precompile(data, context);
    }

    public record Compiled(PreCompiled preCompiled,
                           ItemStackRestrictionResolver resolver) implements CompiledRestrictionEntry<Compiled, PreCompiled> {
        @Override
        public CommonItemCollection gameContent() {
            return preCompiled().gameContent();
        }
    }

    public record PreCompiled(CommonItemCollection gameContent,
                              ItemStackRestrictionResolverFactory.PreCompiled preCompiledItemStack,
                              RestrictionEntryOrigin origin,
                              DataDrivenNetwork.NetworkData<?> dataDrivenData) implements RestrictionEntry.PreCompiled<PreCompiled, Compiled> {
        @Override
        public Compiled compile(PlayerCompilationTask task) {
            var resolver = preCompiledItemStack.compile(task);
            return new Compiled(this, resolver);
        }

        @Override
        public CustomPacket createPacket(ServerGameStageManager instance) {
            return new CommonItemRestrictionPacket(gameContent(), origin().toString(), dataDrivenData);
        }
    }
}
