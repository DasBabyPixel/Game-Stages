package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs;

import de.dasbabypixel.gamestages.common.data.GameContent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record SomeContentWrapper(GameContent content) implements ContentWrapper {
}
