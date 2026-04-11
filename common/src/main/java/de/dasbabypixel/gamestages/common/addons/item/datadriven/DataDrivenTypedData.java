package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record DataDrivenTypedData<Data extends DataDrivenData>(String type, Data data) {
}
