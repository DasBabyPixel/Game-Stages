package de.dasbabypixel.gamestages.common.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.addons.item.AbstractItemRestrictionEntry;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolver;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactories;
import de.dasbabypixel.gamestages.common.addons.item.ItemStackRestrictionResolverFactory;
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
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.CommonItemRestrictionPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenNetwork;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public final class CommonItemRestrictionEntry extends AbstractItemRestrictionEntry<CommonItemRestrictionEntry, CommonItemRestrictionEntry.PreCompiled> {
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
    public PreCompiled precompile(AbstractGameStageManager<?> instance, RestrictionEntryPreCompiler preCompiler) {
        var items = instance
                .get(GameContentFlattener.Attribute.INSTANCE)
                .flatten(targetItems(), CommonItemCollection.TYPE);
        return new PreCompiled(items);
    }

    @Override
    public CompiledRestrictionEntry compile(RecompilationTask task, PreCompiled preCompiled) {
        var networkData = dataDrivenNetworkData();
        var factoryId = networkData.factoryId();
        var factory = ItemStackRestrictionResolverFactories.instance().getFactory(factoryId);
        if (factory == null) throw new IllegalStateException("Unknown factory " + factoryId);
        var resolver = compile(factory, task, networkData.data().toTypedData());
        return new Compiled(this, preCompiled.items, resolver);
    }

    @SuppressWarnings("unchecked")
    private <T> ItemStackRestrictionResolver compile(ItemStackRestrictionResolverFactory<T> factory, RecompilationTask task, DataDrivenTypedData<?> data) {
        var map = ((VItemAddon.ItemCompileContext) task.getContext(VItemAddon.instance())).factoryContextMap();
        var ctx = (T) Objects.requireNonNull(map.get(factory));
        return factory.compile(data, ctx);
    }

    public record Compiled(CommonItemRestrictionEntry entry, CommonItemCollection gameContent,
                           ItemStackRestrictionResolver resolver) implements CompiledRestrictionEntry {
        @Override
        public RestrictionEntryOrigin origin() {
            return entry.origin();
        }
    }

    public record PreCompiled(CommonItemCollection items) {
    }
}
