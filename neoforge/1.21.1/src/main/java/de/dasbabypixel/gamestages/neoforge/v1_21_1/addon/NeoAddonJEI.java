package de.dasbabypixel.gamestages.neoforge.v1_21_1.addon;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.TypedGameContent;
import de.dasbabypixel.gamestages.common.data.restriction.compiled.CompiledRestrictionEntry;
import de.dasbabypixel.gamestages.common.v1_21_1.data.CommonGameContentType;
import mezz.jei.api.runtime.IJeiRuntime;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public interface NeoAddonJEI {
    default <T> void updateVisibility(boolean newVisibility, T content, Consumer<? super T> show, Consumer<? super T> hide) {
        if (newVisibility) {
            show.accept(content);
        } else {
            hide.accept(content);
        }
    }

    default <T extends TypedGameContent> void iterate(BaseStages stages, CommonGameContentType<T> type, Consumer<CompiledRestrictionEntry> consumer) {
        var typeIndex = stages.typeIndexMap().get(type);
        if (typeIndex == null) return;
        for (var entry : typeIndex.contentListByEntry().keySet()) {
            consumer.accept(entry);
        }
    }

    default void onRuntimeAvailable(IJeiRuntime runtime) {
    }

    default void onRuntimeUnavailable() {
    }

    default void postCompileAll(AbstractGameStageManager instance, BaseStages stages) {
    }

    default void singleRefreshAll(AbstractGameStageManager instance, BaseStages stages) {
    }
}
