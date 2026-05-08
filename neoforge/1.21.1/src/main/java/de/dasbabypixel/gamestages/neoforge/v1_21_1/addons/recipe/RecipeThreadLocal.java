package de.dasbabypixel.gamestages.neoforge.v1_21_1.addons.recipe;

import de.dasbabypixel.gamestages.common.data.BaseStages;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class RecipeThreadLocal {
    private static final ThreadLocal<RecipeThreadLocal> THREAD_LOCAL = ThreadLocal.withInitial(RecipeThreadLocal::new);

    private @Nullable BaseStages stages;

    public @Nullable BaseStages stagesOrRecord() {
        return stages;
    }

    public void stages(@Nullable BaseStages stages) {
        this.stages = stages;
    }

    public void clearStages() {
        stages = null;
    }

    public static RecipeThreadLocal get() {
        return THREAD_LOCAL.get();
    }
}
