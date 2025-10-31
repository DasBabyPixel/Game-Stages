package de.dasbabypixel.gamestages.common.data.restriction.types;

import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import org.jspecify.annotations.NonNull;

public interface RestrictionEntry<T extends RestrictionEntry<T>> {
    @NonNull PreparedRestrictionPredicate predicate();

    @NonNull T disallowDuplicates();

    @NonNull T allowDuplicates();

    @NonNull CustomPacket createPacket();

    @SuppressWarnings("unchecked")
    default @NonNull T self() {
        return (T) this;
    }
}
