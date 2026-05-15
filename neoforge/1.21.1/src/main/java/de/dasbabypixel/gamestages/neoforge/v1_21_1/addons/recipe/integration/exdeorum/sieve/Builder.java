package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve;

import org.jspecify.annotations.NullMarked;

@NullMarked
interface Builder<T> {
    boolean canBuild();

    T build();
}
