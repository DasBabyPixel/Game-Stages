package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.data.restriction.DuplicateReport;

import java.util.List;
import java.util.function.Consumer;

public class DuplicatesException extends RuntimeException {
    private final List<DuplicateReport> duplicates;

    public DuplicatesException(List<DuplicateReport> duplicates) {
        this.duplicates = duplicates;
    }

    public void print(Consumer<String> lineWriter) {
        lineWriter.accept("[GameStages] Found Duplicates (" + duplicates.size() + ")");
        duplicates.forEach(r -> r.print(lineWriter));
    }

    public List<DuplicateReport> duplicates() {
        return duplicates;
    }
}
