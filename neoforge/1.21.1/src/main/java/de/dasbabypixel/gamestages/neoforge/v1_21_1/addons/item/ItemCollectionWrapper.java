package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.item;

import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.ContentWrapper;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record ItemCollectionWrapper(GameContent content) implements ContentWrapper {
}
