package de.dasbabypixel.gamestages.common.data.server;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.attribute.Attribute;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

@NullMarked
public abstract class MutableGameStageManager extends AbstractGameStageManager<MutableGameStageManager> {
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

    protected Map<Attribute<? super MutableGameStageManager, ?>, Object> attributeMap() {
        return attributeMap;
    }
}
