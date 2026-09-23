package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface GameCollectionJS {
    GameCollectionJS except(GameCollectionJS... other);

    GameCollectionJS only(GameCollectionJS... other);

    GameCollectionJS union(GameCollectionJS... other);

    TypedGameCollectionJS filterType(GameCollectionTypeJS type);
}
