package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.addons.recipe.RecipeCollection;
import de.dasbabypixel.gamestages.common.data.PlayerCompilationTask;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.manager.immutable.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.AbstractMutableGameStageManager;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeRestrictionEntry;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeRestrictionPacket;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class NeoRecipeRestrictionEntry extends CommonRecipeRestrictionEntry<NeoRecipeRestrictionEntry, NeoRecipeRestrictionEntry.PreCompiled, NeoRecipeRestrictionEntry.Compiled> {
    public NeoRecipeRestrictionEntry(PreparedRestrictionPredicate predicate, RestrictionEntryOrigin origin, RecipeCollection targetRecipes) {
        super(predicate, origin, targetRecipes);
    }

    @Override
    public NeoRecipeRestrictionEntry copyWith(PreparedRestrictionPredicate predicate, RecipeCollection targetRecipes) {
        return new NeoRecipeRestrictionEntry(predicate, origin(), targetRecipes);
    }

    @Override
    public PreCompiled precompile(AbstractMutableGameStageManager<?> instance) {
        var recipes = instance.get(GameContentFlattener.Attribute.INSTANCE)
                .flatten(targetRecipes(), CommonRecipeCollection.TYPE);
        return new PreCompiled(this, predicate(), recipes, hideInJEI());
    }

    public record Compiled(PreCompiled preCompiled, CompiledRestrictionPredicate predicate,
                           boolean hideInJEI) implements CompiledRestrictionEntry<Compiled, PreCompiled> {
    }

    public record PreCompiled(NeoRecipeRestrictionEntry entry, PreparedRestrictionPredicate predicate,
                              CommonRecipeCollection gameContent,
                              boolean hideInJEI) implements RestrictionEntry.PreCompiled<PreCompiled, Compiled> {
        @Override
        public Compiled compile(PlayerCompilationTask task) {
            return new Compiled(this, task.predicateCompiler().compile(predicate), hideInJEI);
        }

        @Override
        public CustomPacket createPacket(ServerGameStageManager instance) {
            return new CommonRecipeRestrictionPacket(predicate(), gameContent(), origin().toString());
        }
    }
}
