package de.dasbabypixel.gamestages.common.data.restriction;

import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class DuplicateReport {
    private final @NonNull Object object;
    private final @NonNull Set<@NonNull CompiledRestrictionEntry> entries = new HashSet<>();

    public DuplicateReport(@NonNull Object object, @NonNull CompiledRestrictionEntry mainEntry, @NonNull Set<@NonNull CompiledRestrictionEntry> duplicates) {
        this.object = object;
        this.entries.add(mainEntry);
        this.entries.addAll(duplicates);
    }

    public void print(@NonNull Consumer<String> lineWriter) {
        lineWriter.accept("For: " + object);
        for (var entry : entries) {
            lineWriter.accept(" - " + entry.origin());
        }
    }
}
