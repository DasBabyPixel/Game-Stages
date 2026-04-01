package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.addon.AddonManager;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.restriction.DuplicateReport;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionEntryCompiler;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionPredicateCompiler;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class RecompilationTask {
    private final @NonNull BaseStages stages;
    private final @NonNull RestrictionEntryCompiler restrictionEntryCompiler;
    private final @NonNull AbstractGameStageManager instance;
    private final @NonNull RestrictionPredicateCompiler predicateCompiler;
    private final @NonNull Map<Addon, Object> addonContext = new HashMap<>();

    public RecompilationTask(@NonNull BaseStages stages, @NonNull AbstractGameStageManager instance) {
        this.stages = stages;
        this.instance = instance;
        this.restrictionEntryCompiler = new RestrictionEntryCompiler(this);
        this.predicateCompiler = new RestrictionPredicateCompiler(stages);
    }

    public void setContext(@NonNull Addon addon, @NonNull Object context) {
        addonContext.put(addon, context);
    }

    public @NonNull Object getContext(Addon addon) {
        return Objects.requireNonNull(addonContext.get(addon));
    }

    public @NonNull AbstractGameStageManager instance() {
        return instance;
    }

    public BaseStages stages() {
        return stages;
    }

    public @NonNull RestrictionPredicateCompiler predicateCompiler() {
        return predicateCompiler;
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
                typeIndex
                        .contentListByEntry()
                        .putIfAbsent(compiledEntry, Objects.requireNonNull(List.copyOf(contents)));
                var entryByContent = typeIndex.entryByContent();

                for (var content : contents) {
                    assert content != null;
                    if (entryByContent.containsKey(content)) {
                        // Duplicate
                        typeIndex.duplicates().computeIfAbsent(content, ignored -> new HashSet<>()).add(compiledEntry);
                    } else {
                        entryByContent.put(content, compiledEntry);
                    }
                }
            }
        }

        var reports = new ArrayList<@NonNull DuplicateReport>();

        for (var entry : typeIndexMap.entrySet()) {
            assert entry != null;
            var typeIndex = entry.getValue();
            assert typeIndex != null;
            var duplicates = typeIndex.duplicates();
            if (!duplicates.isEmpty()) {
                for (var duplicateEntry : duplicates.entrySet()) {
                    assert duplicateEntry != null;
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
        for (var addon : AddonManager.instance().addons()) {
            addon.preCompileAll(this);
        }
        recompileGameStages();
        recompileEntries();
    }

    public void firePostCompile() {
        for (var addon : AddonManager.instance().addons()) {
            addon.postCompileAll(this);
        }
    }

    private void recompileEntries() {
        stages.compiledRestrictionEntryMap().clear();
        for (var restriction : instance.restrictions()) {
            // Compiling also links dependencies
            var compiledEntry = restrictionEntryCompiler.compile(restriction);

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
