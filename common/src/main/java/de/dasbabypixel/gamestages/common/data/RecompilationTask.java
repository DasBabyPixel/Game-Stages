package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.addon.AddonManager;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.restriction.DuplicateReport;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionEntryCompiler;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionPredicateCompiler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class RecompilationTask {
    private final BaseStages stages;
    private final RestrictionEntryCompiler restrictionEntryCompiler;
    private final AbstractGameStageManager instance;
    private final RestrictionPredicateCompiler predicateCompiler;

    public RecompilationTask(BaseStages stages, RestrictionEntryCompiler restrictionEntryCompiler, AbstractGameStageManager instance) {
        this.stages = stages;
        this.restrictionEntryCompiler = restrictionEntryCompiler;
        this.instance = instance;
        this.predicateCompiler = new RestrictionPredicateCompiler(stages);
    }

    public void findDuplicates() {
        var flattener = instance.get(GameContentFlattener.Attribute.INSTANCE);

        var typeIndexMap = stages.typeIndexMap();
        typeIndexMap.clear();

        var compiledMap = stages.compiledRestrictionEntryMap();
        for (var compiledEntry : compiledMap.values()) {
            var flattened = flattener.flatten(compiledEntry.gameContent());
            for (var type : flattened.types()) {
                var typed = flattened.get(type);
                if (typed.isEmpty()) continue;
                var typeIndex = typeIndexMap.computeIfAbsent(type, ignored -> new BaseStages.TypeIndex());

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
            stages.typeIndexMap().clear();
            stages.compiledRestrictionEntryMap().clear();
            throw new DuplicatesException(reports);
        }
    }

    public void recompile() {
        recompileGameStages();
        recompileEntries();
    }

    public void firePostCompile() {
        for (var addon : AddonManager.instance().addons()) {
            addon.postCompileAll(instance, stages);
        }
    }

    private void recompileEntries() {
        stages.compiledRestrictionEntryMap().clear();
        for (var restriction : instance.restrictions()) {
            var predicate = restriction.predicate();
            // Compiling also links dependencies
            var compiledPredicate = predicateCompiler.compile(predicate);
            var compiledEntry = restrictionEntryCompiler.compile(restriction, compiledPredicate);

            for (var addon : AddonManager.instance().addons()) {
                addon.postCompile(compiledEntry);
            }

            stages.compiledRestrictionEntryMap().put(restriction, compiledEntry);
        }
    }

    private void recompileGameStages() {
        stages.compiledGameStages().clear();
        for (var gameStage : instance.gameStages()) {
            var compiled = predicateCompiler.compile(gameStage);
            stages.compiledGameStages().put(gameStage, compiled);
        }
    }
}
