package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NonNull;

public interface ItemCollection<T extends ItemCollection<T>> {
    @NonNull T except(@NonNull ItemCollection<T> other);

    @NonNull T only(@NonNull ItemCollection<T> other);

    @NonNull T union(@NonNull ItemCollection<T> other);

    @SuppressWarnings("unchecked")
    default @NonNull T self() {
        return (T) this;
    }
}
