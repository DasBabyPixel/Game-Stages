package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.addon.Addon;
import de.dasbabypixel.gamestages.common.addon.AddonManager;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.restriction.DuplicateReport;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionEntryCompiler;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionPredicateCompiler;
import org.jspecify.annotations.NullMarked;

import java.util.*;

@NullMarked
public class RecompilationTask {
    private final BaseStages stages;
    private final RestrictionEntryCompiler restrictionEntryCompiler;
    private final AbstractGameStageManager<?> instance;
    private final RestrictionPredicateCompiler predicateCompiler;
    private final Map<Addon, Object> addonContext = new HashMap<>();

    public RecompilationTask(BaseStages stages, AbstractGameStageManager<?> instance) {
        this.stages = stages;
        this.instance = instance;
        this.restrictionEntryCompiler = new RestrictionEntryCompiler(this);
        this.predicateCompiler = new RestrictionPredicateCompiler(stages);
    }

    public void setContext(Addon addon, Object context) {
        addonContext.put(addon, context);
    }

    public Object getContext(Addon addon) {
        return Objects.requireNonNull(addonContext.get(addon));
    }

    public AbstractGameStageManager<?> instance() {
        return instance;
    }

    public BaseStages stages() {
        return stages;
    }

    public RestrictionPredicateCompiler predicateCompiler() {
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

        var reports = new ArrayList<DuplicateReport>();

        for (var entry : typeIndexMap.entrySet()) {
            assert entry != null;
            var typeIndex = entry.getValue();
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
                addon.postCompile(this, compiledEntry);
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
