package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.jsapi;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface TypedGameCollectionJS extends GameCollectionJS {
    GameCollectionTypeJS type();

    @Override
    TypedGameCollectionJS except(GameCollectionJS... other);

    @Override
    TypedGameCollectionJS only(GameCollectionJS... other);

    @Override
    GameCollectionJS union(GameCollectionJS... other);
}
