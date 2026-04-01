package de.dasbabypixel.gamestages.common.addons.item.datadriven;

import org.jspecify.annotations.NonNull;

public record DataDrivenTypedData<Data extends DataDrivenData>(@NonNull String type, Data data) {
}
