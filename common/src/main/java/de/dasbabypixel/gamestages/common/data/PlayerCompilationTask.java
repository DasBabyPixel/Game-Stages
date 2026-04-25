package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.data.attribute.AbstractAttributeHolder;
import de.dasbabypixel.gamestages.common.data.manager.immutable.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.immutable.PreCompileIndex;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionPredicateCompiler;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
        var typeIndexMap = new HashMap<GameContentType<?>, BaseStages.MutableTypeIndex>();
        for (var type : GameContentType.TYPES) {
            typeIndexMap.put(type, new BaseStages.MutableTypeIndex(type));
        }
        var compiledRestrictionEntries = new ArrayList<CompiledRestrictionEntry<?, ?>>();
        for (var restriction : preCompileIndex.preCompiledRestrictions()) {
            var compiledEntry = restriction.compile(this);

            compiledRestrictionEntries.add(compiledEntry);
            var type = restriction.gameContent().type();
            var typeIndex = Objects.requireNonNull(typeIndexMap.get(type));
            var contentList = List.<Object>copyOf(compiledEntry.gameContent().contentCollection());
            typeIndex.contentListByEntry().put(compiledEntry, contentList);
            for (var content : contentList) {
                if (typeIndex.entryByContent().containsKey(content)) throw new IllegalStateException();
                typeIndex.entryByContent().put(content, compiledEntry);
            }

            COMPILE_POST_EVENT.call(new CompilePostEvent(this, compiledEntry));
        }
        compileIndex.initCompiledRestrictionEntries(compiledRestrictionEntries);
        for (var index : typeIndexMap.values()) {
            compileIndex.initTypeIndex(index.compile());
        }
    }

    private void compileGameStages(BaseStages.CompileIndex compileIndex) {
        var gameStageMap = new HashMap<GameStage, CompiledRestrictionPredicate>();
        for (var gameStage : manager.gameStages()) {
            var compiled = predicateCompiler.compile(gameStage);
            gameStageMap.put(gameStage, compiled);
        }
        compileIndex.initGameStages(gameStageMap);
    }
}
