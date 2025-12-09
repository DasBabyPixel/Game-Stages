package de.dasbabypixel.gamestages.common.data.restriction;

import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class DuplicateReport {
    private final Object object;
    private final Set<CompiledRestrictionEntry> entries = new HashSet<>();

    public DuplicateReport(Object object, CompiledRestrictionEntry mainEntry, Set<CompiledRestrictionEntry> duplicates) {
        this.object = object;
        this.entries.add(mainEntry);
        this.entries.addAll(duplicates);
    }

    public void print(Consumer<String> lineWriter) {
        lineWriter.accept("For: " + object);
        for (var entry : entries) {
            lineWriter.accept(" - " + entry.predicate().predicate() + " (" + entry.origin() + ")");
        }
    }
}
