package de.dasbabypixel.gamestages.common.data.logicng;

import de.dasbabypixel.gamestages.common.data.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.BaseStages;
import de.dasbabypixel.gamestages.common.data.attribute.Attribute;
import org.jspecify.annotations.NullMarked;
import org.logicng.formulas.FormulaFactory;
import org.logicng.transformations.simplification.AdvancedSimplifier;

@NullMarked
public class LogicNG {
    public static final Attribute<AbstractGameStageManager<?>, LogicNG> ATTRIBUTE = new Attribute<>(LogicNG::new);
    public static final Attribute<BaseStages, Stages> STAGES_ATTRIBUTE = new Attribute<>(Stages::new);

    private final FormulaFactory formulaFactory = new FormulaFactory();
    private final AdvancedSimplifier simplifier = new AdvancedSimplifier();

    private LogicNG() {
    }

    public FormulaFactory formulaFactory() {
        return formulaFactory;
    }

    public AdvancedSimplifier simplifier() {
        return simplifier;
    }

    public static class Stages {
        private final BaseStages stages;

        public Stages(BaseStages stages) {
            this.stages = stages;
        }
    }
}
