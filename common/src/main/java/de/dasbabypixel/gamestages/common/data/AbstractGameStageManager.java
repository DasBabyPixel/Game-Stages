package de.dasbabypixel.gamestages.common.data;

import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractGameStageManager {
    protected final Map<GameStageReference, GameStage> gameStages = new HashMap<>();

    public void add(GameStage gameStage) {
        if (!mayMutate()) throw new IllegalStateException("Cannot mutate");
        var ref = new GameStageReference(gameStage.name());
        if (containsKey(ref)) {
            throw new IllegalArgumentException("Multiple GameStages have the same name");
        }
        put0(ref, gameStage);
    }

    public @Nullable GameStage get(String name) {
        return get0(new GameStageReference(name));
    }

    public void set(List<GameStage> gameStages) {
        reset();
        gameStages.forEach(this::add);
    }

    public void reset() {
        clear0();
    }

    public Map<GameStageReference, GameStage> getGameStages() {
        return gameStages;
    }

    protected void clear0() {
        gameStages.clear();
    }

    protected boolean containsKey(GameStageReference reference) {
        return gameStages.containsKey(reference);
    }

    protected void put0(GameStageReference reference, GameStage stage) {
        gameStages.put(reference, stage);
    }

    protected GameStage get0(GameStageReference reference) {
        return gameStages.get(reference);
    }

    protected boolean mayMutate() {
        return true;
    }
}
