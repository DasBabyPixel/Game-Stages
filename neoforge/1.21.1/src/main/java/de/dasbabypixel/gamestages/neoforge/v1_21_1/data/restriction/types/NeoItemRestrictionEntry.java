package de.dasbabypixel.gamestages.neoforge.v1_21_1.data.restriction.types;

import de.dasbabypixel.gamestages.common.data.ItemCollection;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonItemCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.data.restriction.types.CommonItemRestrictionEntry;
import de.dasbabypixel.gamestages.common.v1_21_1.network.packets.clientbound.CommonItemRestrictionPacket;
import org.jspecify.annotations.NonNull;

public class NeoItemRestrictionEntry extends CommonItemRestrictionEntry<NeoItemRestrictionEntry> {
    public NeoItemRestrictionEntry(@NonNull PreparedRestrictionPredicate predicate, @NonNull ItemCollection<?> targetCollection) {
        super(predicate, targetCollection);
    }

    @Override
    public @NonNull CustomPacket createPacket() {
        return new CommonItemRestrictionPacket(predicate(), (CommonItemCollection<?>) targetCollection(), hideTooltip(), renderItemName(), hideInJEI());
    }
}
