package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.data.attribute.AbstractAttributeHolder;
import de.dasbabypixel.gamestages.common.data.manager.immutable.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.immutable.PreCompileIndex;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionPredicateCompiler;
import org.jspecify.annotations.NullMarked;

import java.util.List;

import static de.dasbabypixel.gamestages.common.addon.Addon.COMPILE_ALL_POST_EVENT;
import static de.dasbabypixel.gamestages.common.addon.Addon.COMPILE_ALL_PRE_EVENT;
import static de.dasbabypixel.gamestages.common.addon.Addon.COMPILE_POST_EVENT;
import static de.dasbabypixel.gamestages.common.addon.Addon.CompileAllPostEvent;
import static de.dasbabypixel.gamestages.common.addon.Addon.CompileAllPreEvent;
import static de.dasbabypixel.gamestages.common.addon.Addon.CompilePostEvent;

@NullMarked
public class PlayerCompilationTask extends AbstractAttributeHolder<PlayerCompilationTask> {
    private final BaseStages stages;
    private final AbstractGameStageManager<?> manager;
    private final RestrictionPredicateCompiler predicateCompiler;

    public PlayerCompilationTask(BaseStages stages, AbstractGameStageManager<?> manager) {
        this.stages = stages;
        this.manager = manager;
        this.predicateCompiler = new RestrictionPredicateCompiler(stages, manager);
    }

    public AbstractGameStageManager<?> manager() {
        return manager;
    }

    public BaseStages stages() {
        return stages;
    }

    public RestrictionPredicateCompiler predicateCompiler() {
        return predicateCompiler;
    }

    public void compile() {
        var compileIndex = stages.get(BaseStages.CompileIndex.ATTRIBUTE);
        compileIndex.clear();
        COMPILE_ALL_PRE_EVENT.call(new CompileAllPreEvent(this));
        compileGameStages(compileIndex);
        compileEntries(compileIndex);
        COMPILE_ALL_POST_EVENT.call(new CompileAllPostEvent(this));
    }

    private void compileEntries(BaseStages.CompileIndex compileIndex) {
        var preCompileIndex = manager.get(PreCompileIndex.ATTRIBUTE);
        compileIndex.compiledRestrictionEntries().clear();
        var typeIndexMap = compileIndex.typeIndexMap();
        for (var restriction : preCompileIndex.preCompiledRestrictions()) {
            var compiledEntry = restriction.compile(this);

            compileIndex.compiledRestrictionEntries().add(compiledEntry);
            var type = restriction.gameContent().type();
            var typeIndex = typeIndexMap.computeIfAbsent(type, ignored -> new BaseStages.TypeIndex());
            typeIndex.contentListByEntry()
                    .put(compiledEntry, List.copyOf(compiledEntry.gameContent().contentCollection()));

            COMPILE_POST_EVENT.call(new CompilePostEvent(this, compiledEntry));
        }
    }

    private void compileGameStages(BaseStages.CompileIndex compileIndex) {
        compileIndex.compiledGameStages().clear();
        for (var gameStage : manager.gameStages()) {
            var compiled = predicateCompiler.compile(gameStage);
            compileIndex.compiledGameStages().put(gameStage, compiled);
        }
    }
}
