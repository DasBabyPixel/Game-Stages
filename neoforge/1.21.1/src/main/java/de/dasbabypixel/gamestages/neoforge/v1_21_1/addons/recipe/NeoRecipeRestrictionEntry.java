package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.GameContent;
import de.dasbabypixel.gamestages.common.data.RecompilationTask;
import de.dasbabypixel.gamestages.common.data.flattening.GameContentFlattener;
import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntryOrigin;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionPredicate;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.RestrictionEntryPreCompiler;
import de.dasbabypixel.gamestages.common.data.server.ServerGameStageManager;
import de.dasbabypixel.gamestages.common.network.CustomPacket;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeCollection;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeRestrictionEntry;
import de.dasbabypixel.gamestages.common.v1_21_1.addons.recipe.CommonRecipeRestrictionPacket;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class NeoRecipeRestrictionEntry extends CommonRecipeRestrictionEntry<NeoRecipeRestrictionEntry, NeoRecipeRestrictionEntry.PreCompiled> {
    public NeoRecipeRestrictionEntry(PreparedRestrictionPredicate predicate, RestrictionEntryOrigin origin, GameContent targetRecipes) {
        super(predicate, origin, targetRecipes);
    }

    @Override
    public CustomPacket createPacket(ServerGameStageManager instance) {
        var recipes = instance
                .get(GameContentFlattener.Attribute.INSTANCE)
                .flatten(targetRecipes(), CommonRecipeCollection.TYPE);
        return new CommonRecipeRestrictionPacket(predicate(), recipes, origin().toString());
    }

    @Override
    public PreCompiled precompile(AbstractGameStageManager instance, RestrictionEntryPreCompiler preCompiler) {
        var recipes = instance
                .get(GameContentFlattener.Attribute.INSTANCE)
                .flatten(targetRecipes(), CommonRecipeCollection.TYPE);
        return new PreCompiled(recipes);
    }

    @Override
    public CompiledRestrictionEntry compile(RecompilationTask task, NeoRecipeRestrictionEntry.PreCompiled preCompiled) {
        return new Compiled(this, preCompiled.recipes, task.predicateCompiler().compile(predicate()));
    }

    public record Compiled(NeoRecipeRestrictionEntry entry, CommonRecipeCollection gameContent,
                           CompiledRestrictionPredicate predicate) implements CompiledRestrictionEntry {
        @Override
        public RestrictionEntryOrigin origin() {
            return entry.origin();
        }
    }

    public record PreCompiled(CommonRecipeCollection recipes) {
    }
}
