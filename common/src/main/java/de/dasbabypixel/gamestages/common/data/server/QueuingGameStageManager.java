package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.GameStage;
import de.dasbabypixel.gamestages.common.data.restriction.RestrictionEntry;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@NullMarked
public class QueuingGameStageManager extends MutableGameStageManager {
    public static final QueuingGameStageManager INSTANCE = new QueuingGameStageManager();
    private final List<Consumer<MutableGameStageManager>> recorded = new ArrayList<>();

    @Override
    public void add(GameStage gameStage) {
        super.add(gameStage);
        recorded.add(c -> c.add(gameStage));
    }

    @Override
    public void reset() {
        super.reset();
        recorded.add(AbstractGameStageManager::reset);
    }

    @Override
    public <T extends RestrictionEntry<T, ?, ?>> T addRestriction(T restriction) {
        super.addRestriction(restriction);
        recorded.add(c -> c.addRestriction(restriction));
        return restriction;
    }

    @Override
    public void set(List<GameStage> gameStages) {
        super.set(gameStages);
        recorded.add(c -> c.set(gameStages));
    }

    @Override
    public void allowMutation() {
        super.allowMutation();
        recorded.add(MutableGameStageManager::allowMutation);
    }

    @Override
    public void disallowMutation() {
        super.disallowMutation();
        recorded.add(MutableGameStageManager::disallowMutation);
    }

    public void begin() {
        gameStages.clear();
    }

    public void end(ServerGameStageManager gameStageManager) {
        if (!gameStageManager.attributeMap().isEmpty()) throw new IllegalStateException();
        recorded.forEach(c -> c.accept(gameStageManager));
        recorded.clear();
        gameStageManager.initAttributeMap(this.attributeMap);
        gameStageManager.attributeMap().putAll(this.attributeMap());
        this.attributeMap.clear();
    }
}
