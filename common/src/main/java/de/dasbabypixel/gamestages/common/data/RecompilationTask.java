package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionEntryCompiler;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionPredicateCompiler;
import de.dasbabypixel.gamestages.common.entity.Player;

import java.util.*;

public class RecompilationTask {
    private final PlayerStages playerStages;
    private final RestrictionEntryCompiler restrictionEntryCompiler;
    private final AbstractGameStageManager instance;
    private final RestrictionPredicateCompiler predicateCompiler;

    public RecompilationTask(PlayerStages playerStages, RestrictionEntryCompiler restrictionEntryCompiler, AbstractGameStageManager instance, Player player) {
        this.playerStages = playerStages;
        this.restrictionEntryCompiler = restrictionEntryCompiler;
        this.instance = instance;
        this.predicateCompiler = new RestrictionPredicateCompiler(player);
    }

    public void findDuplicates() {
        var flattener = instance.get(GameContentFlattener.Attribute.INSTANCE);

        var typeIndexMap = playerStages.typeIndexMap();
        typeIndexMap.clear();

        var compiledMap = playerStages.compiledRestrictionEntryMap();
        for (var compiledEntry : compiledMap.values()) {
            var flattened = flattener.flatten(compiledEntry.gameContent());
            for (var type : flattened.types()) {
                var typed = flattened.get(type);
                if (typed.isEmpty()) continue;
                var typeIndex = typeIndexMap.computeIfAbsent(type, ignored -> new PlayerStages.TypeIndex());

                var contents = typed.content();
                typeIndex.contentListByEntry().putIfAbsent(compiledEntry, List.copyOf(contents));
                var entryByContent = typeIndex.entryByContent();

                for (var content : contents) {
                    if (entryByContent.containsKey(content)) {
                        // Duplicate
                        typeIndex.duplicates().computeIfAbsent(content, ignored -> new HashSet<>()).add(compiledEntry);
                    } else {
                        entryByContent.put(content, compiledEntry);
                    }
                }
            }
        }

        var reports = new ArrayList<DuplicateReport>();

        for (var entry : typeIndexMap.entrySet()) {
            var typeIndex = entry.getValue();
            var duplicates = typeIndex.duplicates();
            if (!duplicates.isEmpty()) {
                for (var duplicateEntry : duplicates.entrySet()) {
                    var object = duplicateEntry.getKey();
                    var mainEntry = Objects.requireNonNull(typeIndex.entryByContent().get(object));
                    var d = duplicateEntry.getValue();
                    reports.add(new DuplicateReport(object, mainEntry, d));
                }
            }
        }

        if (!reports.isEmpty()) {
            System.err.println("[GameStages] Found Duplicates (" + reports.size() + ")");
            reports.forEach(DuplicateReport::print);
            playerStages.typeIndexMap().clear();
            playerStages.compiledRestrictionEntryMap().clear();
            throw new DuplicatesException();
        }
    }

    public void recompile() {
        recompileGameStages();
        recompileEntries();
    }

    public void firePostCompile() {
        for (var addon : instance.addons()) {
            addon.postCompileAll(instance, playerStages);
        }
    }

    private void recompileEntries() {
        playerStages.compiledRestrictionEntryMap().clear();
        for (var restriction : instance.restrictions()) {
            var predicate = restriction.predicate();
            // Compiling also links dependencies
            var compiledPredicate = predicateCompiler.compile(predicate);
            var compiledEntry = restrictionEntryCompiler.compile(restriction, compiledPredicate);

            for (var addon : instance.addons()) {
                addon.postCompile(compiledEntry);
            }

            playerStages.compiledRestrictionEntryMap().put(restriction, compiledEntry);
        }
    }

    private void recompileGameStages() {
        playerStages.compiledGameStages().clear();
        for (var gameStage : instance.gameStages()) {
            var compiled = predicateCompiler.compile(gameStage);
            playerStages.compiledGameStages().put(gameStage, compiled);
        }
    }

    public static class DuplicateReport {
        private final Object object;
        private final Set<CompiledRestrictionEntry> entries = new HashSet<>();

        public DuplicateReport(Object object, CompiledRestrictionEntry mainEntry, Set<CompiledRestrictionEntry> duplicates) {
            this.object = object;
            this.entries.add(mainEntry);
            this.entries.addAll(duplicates);
        }

        public void print() {
            System.err.println("For: " + object);
            for (var entry : entries) {
                System.err.println(" - " + entry.predicate().predicate() + " (" + entry.origin() + ")");
            }
        }
    }
}
