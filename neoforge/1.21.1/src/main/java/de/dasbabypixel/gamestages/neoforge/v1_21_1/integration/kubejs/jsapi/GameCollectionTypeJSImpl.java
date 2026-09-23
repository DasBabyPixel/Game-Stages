package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi;

import de.dasbabypixel.gamestages.common.data.GameContentRegistry;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.JSContext;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record GameCollectionTypeJSImpl(GameContentRegistry.Entry<?, ?, ?, ?> typeEntry,
                                       JSContext.TypedContentParser parser) implements GameCollectionTypeJS {
    @Override
    public String toString() {
        return typeEntry.id();
    }
}
