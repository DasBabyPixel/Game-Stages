package de.dasbabypixel.gamestages.common.data;

import de.dasbabypixel.gamestages.common.data.attribute.SimpleAttributeHolder;
import de.dasbabypixel.gamestages.common.data.manager.immutable.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.immutable.PreCompileIndex;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionPredicateCompiler;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static de.dasbabypixel.gamestages.common.addon.Addon.COMPILE_ALL_POST_EVENT;
import static de.dasbabypixel.gamestages.common.addon.Addon.COMPILE_ALL_PRE_EVENT;
import static de.dasbabypixel.gamestages.common.addon.Addon.COMPILE_POST_EVENT;
import static de.dasbabypixel.gamestages.common.addon.Addon.CompileAllPostEvent;
import static de.dasbabypixel.gamestages.common.addon.Addon.CompileAllPreEvent;
import static de.dasbabypixel.gamestages.common.addon.Addon.CompilePostEvent;

@NullMarked
public class PlayerCompilationTask extends SimpleAttributeHolder<PlayerCompilationTask> {
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
        stages.reset();
        COMPILE_ALL_PRE_EVENT.call(new CompileAllPreEvent(this));
        var compiledGameStages = compileGameStages();
        var compiledEntries = compileEntries();
        var compileIndex = new BaseStages.CompileIndex(compiledGameStages, compiledEntries.typeIndexMap, compiledEntries.compiledRestrictionEntries);
        stages.init(BaseStages.CompileIndex.ATTRIBUTE, compileIndex);
        for (var value : compiledGameStages.values()) {
            value.test(); // Test all to make sure values are cached
        }
        COMPILE_ALL_POST_EVENT.call(new CompileAllPostEvent(this));
    }

    private CompiledEntries compileEntries() {
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
            var contentList = Objects.requireNonNull(List.<Object>copyOf(compiledEntry.gameContent()
                    .contentCollection()));
            typeIndex.contentListByEntry().put(compiledEntry, contentList);
            for (var content : contentList) {
                if (typeIndex.entryByContent().containsKey(content)) throw new IllegalStateException();
                typeIndex.entryByContent().put(content, compiledEntry);
            }

            COMPILE_POST_EVENT.call(new CompilePostEvent(this, compiledEntry));
        }
        var typeIndexMapC = new HashMap<GameContentType<?>, BaseStages.TypeIndex>();
        for (var index : typeIndexMap.values()) {
            var t = index.compile();
            typeIndexMapC.put(t.type(), t);
        }
        return new CompiledEntries(compiledRestrictionEntries, typeIndexMapC);
    }

    private Map<GameStage, CompiledRestrictionPredicate> compileGameStages() {
        var gameStageMap = new HashMap<GameStage, CompiledRestrictionPredicate>();
        for (var gameStage : manager.gameStages()) {
            var compiled = predicateCompiler.compile(gameStage);
            gameStageMap.put(gameStage, compiled);
        }
        return gameStageMap;
    }

    private record CompiledEntries(List<CompiledRestrictionEntry<?, ?>> compiledRestrictionEntries,
                                   Map<GameContentType<?>, BaseStages.TypeIndex> typeIndexMap) {
    }
}
