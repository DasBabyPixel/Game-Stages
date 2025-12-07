package de.dasbabypixel.gamestages.common.data.restriction.types;

import org.jspecify.annotations.NonNull;

public interface RestrictionEntryOrigin {
    RestrictionEntryOrigin SERVER = string("SERVER");

    static RestrictionEntryOrigin string(String text) {
        return new RestrictionEntryOrigin() {
            @Override
            public @NonNull String toString() {
                return text;
            }
        };
    }

    @NonNull String toString();
}
