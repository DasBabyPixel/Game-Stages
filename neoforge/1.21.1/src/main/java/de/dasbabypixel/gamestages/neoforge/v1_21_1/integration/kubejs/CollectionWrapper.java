package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs;

import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonGameContent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record CollectionWrapper(CommonGameContent content) implements ContentWrapper {
}
