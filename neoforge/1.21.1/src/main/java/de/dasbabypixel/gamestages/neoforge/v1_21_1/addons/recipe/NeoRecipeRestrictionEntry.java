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
import org.jspecify.annotations.NonNull;

public class NeoRecipeRestrictionEntry extends CommonRecipeRestrictionEntry<NeoRecipeRestrictionEntry, NeoRecipeRestrictionEntry.PreCompiled> {
    public NeoRecipeRestrictionEntry(@NonNull PreparedRestrictionPredicate predicate, @NonNull RestrictionEntryOrigin origin, @NonNull GameContent targetRecipes) {
        super(predicate, origin, targetRecipes);
    }

    @Override
    public @NonNull CustomPacket createPacket(@NonNull ServerGameStageManager instance) {
        var recipes = instance
                .get(GameContentFlattener.Attribute.INSTANCE)
                .flatten(targetRecipes(), CommonRecipeCollection.TYPE);
        return new CommonRecipeRestrictionPacket(predicate(), recipes, origin().toString());
    }

    @Override
    public @NonNull PreCompiled precompile(@NonNull AbstractGameStageManager instance, @NonNull RestrictionEntryPreCompiler preCompiler) {
        var recipes = instance
                .get(GameContentFlattener.Attribute.INSTANCE)
                .flatten(targetRecipes(), CommonRecipeCollection.TYPE);
        return new PreCompiled(recipes);
    }

    @Override
    public @NonNull CompiledRestrictionEntry compile(@NonNull RecompilationTask task, NeoRecipeRestrictionEntry.@NonNull PreCompiled preCompiled) {
        return new Compiled(this, preCompiled.recipes, task.predicateCompiler().compile(predicate()));
    }

    public record Compiled(@NonNull NeoRecipeRestrictionEntry entry, @NonNull CommonRecipeCollection gameContent,
                           @NonNull CompiledRestrictionPredicate predicate) implements CompiledRestrictionEntry {
        @Override
        public @NonNull RestrictionEntryOrigin origin() {
            return entry.origin();
        }
    }

    public record PreCompiled(@NonNull CommonRecipeCollection recipes) {
    }
}
