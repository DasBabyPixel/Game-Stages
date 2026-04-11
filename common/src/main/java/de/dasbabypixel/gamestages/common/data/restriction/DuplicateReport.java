package de.dasbabypixel.gamestages.common.data.restriction;

import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@NullMarked
public class DuplicateReport {
    private final Object object;
    private final Set<RestrictionEntry.PreCompiled<?, ?>> entries = new HashSet<>();

    public DuplicateReport(Object object, RestrictionEntry.PreCompiled<?, ?> mainEntry, Set<RestrictionEntry.PreCompiled<?, ?>> duplicates) {
        this.object = object;
        this.entries.add(mainEntry);
        this.entries.addAll(duplicates);
    }

    public void print(Consumer<String> lineWriter) {
        lineWriter.accept("For: " + object);
        for (var entry : entries) {
            lineWriter.accept(" - " + entry.origin());
        }
    }
}
