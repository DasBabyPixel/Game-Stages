package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeRestrictionEntry;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeRestrictionPacket;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class NeoRecipeRestrictionEntry extends CommonRecipeRestrictionEntry<NeoRecipeRestrictionEntry, NeoRecipeRestrictionEntry.PreCompiled, NeoRecipeRestrictionEntry.Compiled> {
    public NeoRecipeRestrictionEntry(PreparedRestrictionPredicate predicate, RestrictionEntryOrigin origin, TypedGameContent targetRecipes) {
        super(predicate, origin, targetRecipes);
    }

    @Override
    public CustomPacket createPacket(ServerGameStageManager instance) {
        var recipes = instance.get(GameContentFlattener.Attribute.INSTANCE)
                .flatten(targetRecipes(), CommonRecipeCollection.TYPE);
        return new CommonRecipeRestrictionPacket(predicate(), recipes, origin().toString());
    }

    @Override
    public PreCompiled precompile(AbstractGameStageManager<?> instance) {
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
        public Compiled compile(RecompilationTask task) {
            return new Compiled(this, task.predicateCompiler().compile(predicate), hideInJEI);
        }
    }
}
