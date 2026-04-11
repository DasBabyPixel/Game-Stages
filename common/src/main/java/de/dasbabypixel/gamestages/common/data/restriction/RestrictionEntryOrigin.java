package de.dasbabypixel.gamestages.common.data.restriction;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface RestrictionEntryOrigin {
    RestrictionEntryOrigin SERVER = string("SERVER");

    String toString();

    static RestrictionEntryOrigin string(String text) {
        return new RestrictionEntryOrigin() {
            @Override
            public String toString() {
                return text;
            }
        };
    }
}
