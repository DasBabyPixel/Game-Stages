package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.data.attribute.AbstractAttributeHolder;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionPredicateCompiler;
import org.jspecify.annotations.NullMarked;

import java.util.List;

import static de.dasbabypixel.gamestages.common.addon.Addon.*;

@NullMarked
public class RecompilationTask extends AbstractAttributeHolder<RecompilationTask> {
    private final BaseStages stages;
    private final AbstractGameStageManager<?> instance;
    private final RestrictionPredicateCompiler predicateCompiler;

    public RecompilationTask(BaseStages stages, AbstractGameStageManager<?> instance) {
        this.stages = stages;
        this.instance = instance;
        this.predicateCompiler = new RestrictionPredicateCompiler(stages);
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

    public void recompile() {
        var compileIndex = stages.get(BaseStages.CompileIndex.ATTRIBUTE);
        compileIndex.clear();
        COMPILE_ALL_PRE_EVENT.call(new CompileAllPreEvent(this));
        recompileGameStages(compileIndex);
        recompileEntries(compileIndex);
        COMPILE_ALL_POST_EVENT.call(new CompileAllPostEvent(this));
    }

    private void recompileEntries(BaseStages.CompileIndex compileIndex) {
        var preCompileIndex = instance.get(AbstractGameStageManager.PreCompileIndex.ATTRIBUTE);
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

    private void recompileGameStages(BaseStages.CompileIndex compileIndex) {
        compileIndex.compiledGameStages().clear();
        for (var gameStage : instance.gameStages()) {
            var compiled = predicateCompiler.compile(gameStage);
            compileIndex.compiledGameStages().put(gameStage, compiled);
        }
    }
}
