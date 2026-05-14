package de.dasbabypixel.gamestages.common.data.manager.mutable.compiler;

import de.dasbabypixel.gamestages.common.addon.Addon.CompileManagerEvent;
import de.dasbabypixel.gamestages.common.addon.Addon.PostCompileTypeEvent;
import de.dasbabypixel.gamestages.common.addon.Addon.PreCompilePrepareEvent;
import de.dasbabypixel.gamestages.common.addon.Addon.PreCompileTypeEvent;
import de.dasbabypixel.gamestages.common.data.DuplicatesException;
import de.dasbabypixel.gamestages.common.data.GameContentType;
import de.dasbabypixel.gamestages.common.data.attribute.AttributeCompiler;
import de.dasbabypixel.gamestages.common.data.attribute.CompilableAttributeHolder;
import de.dasbabypixel.gamestages.common.data.attribute.SimpleAttribute;
import de.dasbabypixel.gamestages.common.data.attribute.SimpleAttributeHolder;
import de.dasbabypixel.gamestages.common.data.manager.immutable.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.immutable.PreCompileIndex;
import de.dasbabypixel.gamestages.common.data.manager.mutable.SimpleMutableGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.DuplicateReport;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static de.dasbabypixel.gamestages.common.addon.Addon.COMPILE_MANAGER_EVENT;
import static de.dasbabypixel.gamestages.common.addon.Addon.POST_COMPILE_TYPE_EVENT;
import static de.dasbabypixel.gamestages.common.addon.Addon.PRE_COMPILE_PREPARE_EVENT;
import static de.dasbabypixel.gamestages.common.addon.Addon.PRE_COMPILE_TYPE_EVENT;

@NullMarked
public final class ManagerCompilerTask extends SimpleAttributeHolder<ManagerCompilerTask> {
    public static final SimpleAttribute<AttributeCompiler<? extends SimpleMutableGameStageManager<?, ?>>, ManagerCompilerTask> ATTRIBUTE = new SimpleAttribute<>();
    private final SimpleMutableGameStageManager<?, ?> manager;
    private final MutablePreCompileIndex preCompileIndex;
    private final Precompiler precompiler = new Precompiler();
    private final HashMap<GameContentType<?>, List<RestrictionEntry<?, ?, ?>>> restrictionsByType = new HashMap<>();

    public ManagerCompilerTask(SimpleMutableGameStageManager<?, ?> manager) {
        this.manager = manager;
        this.preCompileIndex = init(MutablePreCompileIndex.ATTRIBUTE, new MutablePreCompileIndex());
    }

    public MutablePreCompileIndex preCompileIndex() {
        return preCompileIndex;
    }

    public SimpleMutableGameStageManager<?, ?> manager() {
        return manager;
    }

    public void postCompile(CompilableAttributeHolder.CompiledAttributesBuilder<? extends SimpleMutableGameStageManager<?, ?>, ? extends AbstractGameStageManager<?>> builder) {
        COMPILE_MANAGER_EVENT.call(new CompileManagerEvent(this, builder));

        builder.add(PreCompileIndex.ATTRIBUTE, get(MutablePreCompileIndex.ATTRIBUTE).compile());
    }

    private void preparePrecompileRestrictions() {
        PRE_COMPILE_PREPARE_EVENT.call(new PreCompilePrepareEvent(this, precompiler.evaluationDependencies));
    }

    public void precompileRestrictions() {
        preparePrecompileRestrictions();

        var restrictions = manager.restrictions();

        for (var restriction : restrictions) {
            restrictionsByType.computeIfAbsent(restriction.gameContent().type(), ignored -> new ArrayList<>())
                    .add(restriction);
        }

        precompiler.precompile();
    }

    public List<RestrictionEntry<?, ?, ?>> restrictionsByType(GameContentType<?> type) {
        return restrictionsByType.computeIfAbsent(type, ignored -> new ArrayList<>());
    }

    private final class Precompiler {
        private final HashMap<GameContentType<?>, Map<Object, Set<RestrictionEntry.PreCompiled<?, ?>>>> duplicates = new HashMap<>(0);
        private final HashMap<GameContentType<?>, List<GameContentType<?>>> evaluationDependencies = new HashMap<>();
        private final HashSet<GameContentType<?>> compiled = new HashSet<>();

        private void precompile() {
            for (var type : GameContentType.TYPES) {
                precompile(type);
            }

            var reports = collectDuplicateReports(duplicates);
            if (!reports.isEmpty()) {
                throw new DuplicatesException(reports);
            }
        }

        private void precompile(GameContentType<?> type) {
            if (!compiled.add(type)) return;

            if (evaluationDependencies.containsKey(type)) {
                for (var gameContentType : Objects.requireNonNull(evaluationDependencies.get(type))) {
                    precompile(gameContentType);
                }
            }

            PRE_COMPILE_TYPE_EVENT.call(new PreCompileTypeEvent(ManagerCompilerTask.this, type));
            var typeIndex = preCompileIndex.typeIndex(type);
            var restrictions = restrictionsByType(type);
            for (var restriction : restrictions) {
                var preCompiled = restriction.compile(ManagerCompilerTask.this);
                var typed = restriction.gameContent();
                preCompileIndex.preCompiledRestrictions().add(preCompiled);

                typeIndex.entries().add(preCompiled);

                if (typed.isEmpty()) continue;
                for (var content : typed.content()) {
                    if (typeIndex.preCompiledByContent().containsKey(content)) {
                        duplicates.computeIfAbsent(type, ignored -> new HashMap<>())
                                .computeIfAbsent(content, ignored -> new HashSet<>())
                                .add(preCompiled);
                    } else {
                        typeIndex.preCompiledByContent().put(content, preCompiled);
                    }
                }
            }
            POST_COMPILE_TYPE_EVENT.call(new PostCompileTypeEvent(ManagerCompilerTask.this, type));
        }

        private List<DuplicateReport> collectDuplicateReports(Map<GameContentType<?>, Map<Object, Set<RestrictionEntry.PreCompiled<?, ?>>>> duplicatesByType) {
            if (duplicatesByType.isEmpty()) return List.of();
            var reports = new ArrayList<DuplicateReport>();
            for (var entry : duplicatesByType.entrySet()) {
                assert entry != null;
                var typeIndex = preCompileIndex.typeIndex(entry.getKey());
                var duplicates = entry.getValue();
                for (var duplicateEntry : duplicates.entrySet()) {
                    assert duplicateEntry != null;
                    var content = duplicateEntry.getKey();
                    var mainEntry = Objects.requireNonNull(typeIndex.preCompiledByContent().get(content));
                    var otherDuplicates = duplicateEntry.getValue();
                    reports.add(new DuplicateReport(content, mainEntry, otherDuplicates));
                }
            }
            return reports;
        }
    }
}
