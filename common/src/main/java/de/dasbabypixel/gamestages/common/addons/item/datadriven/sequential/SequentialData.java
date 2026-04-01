package de.dasbabypixel.gamestages.common.addons.item.datadriven.sequential;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record SequentialData(@NonNull List<@NonNull DataDrivenTypedData<?>> values) implements DataDrivenData {
}
