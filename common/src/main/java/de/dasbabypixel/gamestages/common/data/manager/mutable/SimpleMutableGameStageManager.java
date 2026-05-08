package de.dasbabypixel.gamestages.common.data.manager.mutable;

import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.attribute.AbstractCompilableAttributeHolder;
import de.dasbabypixel.gamestages.common.data.attribute.AttributeCompiler;
import de.dasbabypixel.gamestages.common.data.attribute.CompilableAttribute;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.logicng.LogicNG;
import de.dasbabypixel.gamestages.common.data.manager.immutable.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.compiler.ManagerCompilerTask;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A game stage manager that is meant to be configured. A new instance should be created when the configuration changes, since a lot of recomputation will need to be done.
 *
 * @param <H>
 */
@NullMarked
public abstract class SimpleMutableGameStageManager<H extends SimpleMutableGameStageManager<H, IM>, IM extends AbstractGameStageManager<IM>> extends AbstractCompilableAttributeHolder<H, IM> {
    public static final CompilableAttribute<SimpleMutableGameStageManager<?, ?>, Set<GameStage>, AbstractGameStageManager<?>> GAME_STAGES = AbstractGameStageManager.GAME_STAGES.compilable((a, v) -> Objects.requireNonNull(Set.copyOf(v)));
    public static final CompilableAttribute<SimpleMutableGameStageManager<?, ?>, List<RestrictionEntry<?, ?, ?>>, AbstractGameStageManager<?>> RESTRICTIONS = AbstractGameStageManager.RESTRICTIONS.compilable((builder, value) -> {
        var compiler = builder.compiler().get(ManagerCompilerTask.ATTRIBUTE);
        return Objects.requireNonNull(Set.copyOf(compiler.preCompileIndex().preCompiledRestrictions()));
    });

    public SimpleMutableGameStageManager() {
        init(GAME_STAGES, new HashSet<>());
        init(RESTRICTIONS, new ArrayList<>());
        init(LogicNG.MUTABLE_MANAGER_ATTRIBUTE, new LogicNG());
        init(GameContentFlattener.Attribute.MUTABLE_MANAGER_ATTRIBUTE, Objects.requireNonNull(GameContentFlattener.Attribute.Factory.FACTORY)
                .get());
    }

    @Override
    public IM compile(AttributeCompiler<H> compiler) {
        compiler.init(ManagerCompilerTask.ATTRIBUTE, new ManagerCompilerTask(this));
        compiler.get(ManagerCompilerTask.ATTRIBUTE).precompileRestrictions();
        return super.compile(compiler);
    }

    @Override
    public void postCompile(CompiledAttributesBuilder<H, IM> builder) {
        super.postCompile(builder);
        var task = builder.compiler().get(ManagerCompilerTask.ATTRIBUTE);
        task.postCompile(builder);
    }

    public void add(GameStage gameStage) {
        if (containsKey(gameStage)) {
            throw new IllegalArgumentException("Multiple GameStages have the same name");
        }
        add0(gameStage);
    }

    public @Nullable GameStage get(String name) {
        var stage = new GameStage(name);
        if (!containsKey(stage)) return null;
        return stage;
    }

    public <T extends RestrictionEntry<T, ?, ?>> T addRestriction(T restriction) {
        restrictions().add(restriction);
        return restriction;
    }

    public void addAll(Collection<? extends GameStage> gameStages) {
        gameStages.forEach(this::add);
    }

    public Set<GameStage> gameStages() {
        return get(GAME_STAGES);
    }

    public List<RestrictionEntry<?, ?, ?>> restrictions() {
        return get(RESTRICTIONS);
    }

    protected boolean containsKey(GameStage gameStage) {
        return gameStages().contains(gameStage);
    }

    protected void add0(GameStage stage) {
        gameStages().add(stage);
    }
}
