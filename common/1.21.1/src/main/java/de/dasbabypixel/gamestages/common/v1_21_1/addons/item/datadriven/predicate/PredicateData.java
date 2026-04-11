package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.predicate;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import net.minecraft.advancements.critereon.ItemPredicate;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record PredicateData(ItemPredicate predicate,
                            ItemStackRestrictionEntryReference resultReference) implements DataDrivenData {
    public static final String TYPE = "predicate";
}
