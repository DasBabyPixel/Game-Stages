package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.data.restriction.DuplicateReport;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Consumer;

public class DuplicatesException extends RuntimeException {
    private final @NonNull List<@NonNull DuplicateReport> duplicates;

    public DuplicatesException(@NonNull List<@NonNull DuplicateReport> duplicates) {
        this.duplicates = duplicates;
    }

    public void print(@NonNull Consumer<String> lineWriter) {
        lineWriter.accept("[GameStages] Found Duplicates (" + duplicates.size() + ")");
        duplicates.forEach(r -> r.print(lineWriter));
    }

    public @NonNull List<@NonNull DuplicateReport> duplicates() {
        return duplicates;
    }
}
