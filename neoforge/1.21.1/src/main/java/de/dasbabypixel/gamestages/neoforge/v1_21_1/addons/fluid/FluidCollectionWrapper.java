package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.fluid;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.ContentWrapper;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record FluidCollectionWrapper(GameContent content) implements ContentWrapper {
}
