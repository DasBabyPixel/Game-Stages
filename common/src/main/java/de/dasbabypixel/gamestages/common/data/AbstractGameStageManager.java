package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractGameStageManager {
    private final Map<GameStageReference, GameStage> gameStages = new HashMap<>();

    public void add(GameStage gameStage) {
        if (!mayMutate()) throw new IllegalStateException("Cannot mutate");
        var ref = new GameStageReference(gameStage.name());
        if (gameStages.containsKey(ref)) {
            throw new IllegalArgumentException("Multiple GameStages have the same name");
        }
        gameStages.put(ref, gameStage);
    }

    public @Nullable GameStage get(String name) {
        return gameStages.get(new GameStageReference(name));
    }

    public void set(List<GameStage> gameStages) {
        this.gameStages.clear();
        gameStages.forEach(this::add);
    }

    public void reset() {
        this.gameStages.clear();
    }

    public Map<GameStageReference, GameStage> getGameStages() {
        return gameStages;
    }

    protected boolean mayMutate() {
        return true;
    }
}
