package de.dasbabypixel.gamestages.common.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.addons.item.AbstractItemRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.item.network.DataDrivenNetwork;
import org.jspecify.annotations.NonNull;

public abstract class CommonItemRestrictionEntry<T extends CommonItemRestrictionEntry<T, P>, P> extends AbstractItemRestrictionEntry<T, P> {
    private final DataDrivenNetwork.@NonNull NetworkData<?> dataDrivenNetworkData;

    public CommonItemRestrictionEntry(@NonNull RestrictionEntryOrigin origin, @NonNull GameContent targetItems, DataDrivenNetwork.@NonNull NetworkData<?> dataDrivenNetworkData) {
        super(origin, targetItems);
        this.dataDrivenNetworkData = dataDrivenNetworkData;
    }

    public DataDrivenNetwork.@NonNull NetworkData<?> dataDrivenNetworkData() {
        return dataDrivenNetworkData;
    }
}
