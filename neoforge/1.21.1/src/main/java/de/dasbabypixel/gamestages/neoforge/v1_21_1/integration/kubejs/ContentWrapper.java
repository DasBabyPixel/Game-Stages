package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.GameContentType;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jspecify.annotations.NonNull;

public interface ContentWrapper {
    @HideFromJS
    GameContent content();

    default @NonNull ContentWrapper except(@NonNull GameContent @NonNull ... other) {
        return new SomeContentWrapper(content().except(other));
    }

    default @NonNull ContentWrapper only(@NonNull GameContent @NonNull ... other) {
        return new SomeContentWrapper(content().only(other));
    }

    default @NonNull ContentWrapper union(@NonNull GameContent @NonNull ... other) {
        return new SomeContentWrapper(content().union(other));
    }

    default @NonNull ContentWrapper filterType(@NonNull GameContentType<?> type) {
        return new SomeContentWrapper(content().filterType(type));
    }
}
