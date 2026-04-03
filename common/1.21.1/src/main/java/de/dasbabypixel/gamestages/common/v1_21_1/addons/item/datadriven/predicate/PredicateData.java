package de.dasbabypixel.gamestages.common.v1_21_1.addons.item.datadriven.predicate;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.ItemStackRestrictionEntryReference;
import net.minecraft.advancements.critereon.ItemPredicate;
import org.jspecify.annotations.NonNull;

public record PredicateData(@NonNull ItemPredicate predicate,
                            @NonNull ItemStackRestrictionEntryReference resultReference) implements DataDrivenData {
}
