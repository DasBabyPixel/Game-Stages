package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;

public abstract class MutatableGameStageManager extends AbstractGameStageManager {
    private boolean allowMutation = false;

    @Override
    protected boolean mayMutate() {
        return allowMutation;
    }

    public void allowMutation() {
        allowMutation = true;
    }

    public void disallowMutation() {
        allowMutation = false;
    }
}
