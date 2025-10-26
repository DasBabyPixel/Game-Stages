package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.GameStage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class QueuingGameStageManager extends MutatableGameStageManager {
    public static final QueuingGameStageManager INSTANCE = new QueuingGameStageManager();
    private final List<Consumer<MutatableGameStageManager>> recorded = new ArrayList<>();

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
    public void set(List<GameStage> gameStages) {
        super.set(gameStages);
        recorded.add(c -> c.set(gameStages));
    }

    @Override
    public void allowMutation() {
        super.allowMutation();
        recorded.add(MutatableGameStageManager::allowMutation);
    }

    @Override
    public void disallowMutation() {
        super.disallowMutation();
        recorded.add(MutatableGameStageManager::disallowMutation);
    }

    public void begin() {
        gameStages.clear();
    }

    public void end(MutatableGameStageManager gameStageManager) {
        recorded.forEach(c -> c.accept(gameStageManager));
        recorded.clear();
    }
}
