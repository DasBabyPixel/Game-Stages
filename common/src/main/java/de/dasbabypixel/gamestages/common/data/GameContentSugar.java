package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.NullMarked;

@NullMarked
public non-sealed interface GameContentSugar extends GameContent {
    GameContent desugar();
}
