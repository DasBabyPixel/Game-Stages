package de.dasbabypixel.gamestages.common.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.addons.item.*;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.CommonItemRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenNetwork;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CommonItemRestrictionEntry extends AbstractItemRestrictionEntry<CommonItemRestrictionEntry, CommonItemRestrictionEntry.PreCompiled, CommonItemRestrictionEntry.Compiled> {
    private final DataDrivenNetwork.NetworkData<?> dataDrivenNetworkData;

    public CommonItemRestrictionEntry(RestrictionEntryOrigin origin, GameContent targetItems, DataDrivenNetwork.NetworkData<?> dataDrivenNetworkData) {
        super(origin, targetItems);
        this.dataDrivenNetworkData = dataDrivenNetworkData;
    }

    @Override
    public CustomPacket createPacket(ServerGameStageManager instance) {
        var items = instance
                .get(GameContentFlattener.Attribute.INSTANCE)
                .flatten(targetItems(), CommonItemCollection.TYPE);
        return new CommonItemRestrictionPacket(items, origin().toString(), dataDrivenNetworkData());
    }

    public DataDrivenNetwork.NetworkData<?> dataDrivenNetworkData() {
        return dataDrivenNetworkData;
    }

    @Override
    public PreCompiled precompile(AbstractGameStageManager<?> instance) {
        var items = instance
                .get(GameContentFlattener.Attribute.INSTANCE)
                .flatten(targetItems(), CommonItemCollection.TYPE);
        var networkData = dataDrivenNetworkData();
        var factoryId = networkData.factoryId();
        var factory = ItemStackRestrictionResolverFactories.instance().getFactory(factoryId);
        if (factory == null) throw new IllegalStateException("Unknown factory " + factoryId);
        var preCompiledItemStack = precompile(instance, factory, networkData.data().toTypedData());
        return new PreCompiled(this, items, preCompiledItemStack);
    }

    private <T> ItemStackRestrictionResolverFactory.PreCompiled precompile(AbstractGameStageManager<?> instance, ItemStackRestrictionResolverFactory<T> factory, DataDrivenTypedData<?> data) {
        var context = instance.get(ItemAddon.PreCompileContext.ATTRIBUTE).get(factory);
        return factory.precompile(data, context);
    }

    public record Compiled(PreCompiled preCompiled,
                           ItemStackRestrictionResolver resolver) implements CompiledRestrictionEntry<Compiled, PreCompiled> {
        @Override
        public CommonItemCollection gameContent() {
            return preCompiled().gameContent();
        }
    }

    public record PreCompiled(CommonItemRestrictionEntry entry, CommonItemCollection gameContent,
                              ItemStackRestrictionResolverFactory.PreCompiled preCompiledItemStack) implements RestrictionEntry.PreCompiled<PreCompiled, Compiled> {
        @Override
        public Compiled compile(RecompilationTask task) {
            var resolver = preCompiledItemStack.compile(task);
            return new Compiled(this, resolver);
        }
    }
}
