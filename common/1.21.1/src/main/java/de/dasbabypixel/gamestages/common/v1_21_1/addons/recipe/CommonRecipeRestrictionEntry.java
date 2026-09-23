package de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.addons.recipe.AbstractRecipeRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.PlayerCompilationTask;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.compiler.ManagerCompilerTask;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CommonRecipeRestrictionEntry extends AbstractRecipeRestrictionEntry<CommonRecipeRestrictionEntry, CommonRecipeRestrictionEntry.PreCompiled, CommonRecipeRestrictionEntry.Compiled> {
    public CommonRecipeRestrictionEntry(PreparedRestrictionPredicate predicate, RestrictionEntryOrigin origin, RecipeContentWrapper gameContent) {
        super(predicate, origin, gameContent);
    }

    public CommonRecipeRestrictionEntry copyWith(PreparedRestrictionPredicate predicate, RecipeContentWrapper gameContent) {
        return new CommonRecipeRestrictionEntry(predicate, origin(), gameContent);
    }

    @Override
    public RecipeContentWrapper gameContent() {
        return (RecipeContentWrapper) super.gameContent();
    }

    @Override
    public PreCompiled compile(ManagerCompilerTask task) {
        return new PreCompiled(predicate(), gameContent(), hideInJEI(), origin());
    }

    public record Compiled(RecipeContentWrapper gameContent, PreCompiled preCompiled,
                           CompiledRestrictionPredicate predicate,
                           boolean hideInJEI) implements CompiledRestrictionEntry<Compiled, PreCompiled> {
    }

    public record PreCompiled(PreparedRestrictionPredicate predicate, RecipeContentWrapper gameContent,
                              boolean hideInJEI,
                              RestrictionEntryOrigin origin) implements RestrictionEntry.PreCompiled<PreCompiled, Compiled> {
        @Override
        public Compiled compile(PlayerCompilationTask task) {
            return new Compiled(gameContent, this, task.predicateCompiler().compile(predicate), hideInJEI);
        }

        @Override
        public CustomPacket createPacket(ServerGameStageManager instance) {
            return new CommonRecipeRestrictionPacket(predicate(), gameContent(), origin().toString());
        }
    }
}
