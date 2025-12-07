package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NonNull;

public interface GameContent {
    @NonNull GameContent except(@NonNull GameContent @NonNull ... other);

    @NonNull GameContent only(@NonNull GameContent @NonNull ... other);

    @NonNull GameContent union(@NonNull GameContent @NonNull ... other);

    @NonNull GameContent filterType(@NonNull GameContentType<?> type);
}
