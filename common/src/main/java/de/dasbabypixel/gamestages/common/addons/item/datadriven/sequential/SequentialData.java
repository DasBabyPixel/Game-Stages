package de.dasbabypixel.gamestages.common.addons.item.datadriven.sequential;

import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenData;
import de.dasbabypixel.gamestages.common.addons.item.datadriven.DataDrivenTypedData;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record SequentialData(List<DataDrivenTypedData<?>> values) implements DataDrivenData {
    public static final String TYPE = "sequential";
}
