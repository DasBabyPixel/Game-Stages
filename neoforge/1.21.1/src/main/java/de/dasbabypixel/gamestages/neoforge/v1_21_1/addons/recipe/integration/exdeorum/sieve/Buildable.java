package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe.integration.exdeorum.sieve;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface Buildable<T> {
    Builder<T> build(BaseStages stages);
}
