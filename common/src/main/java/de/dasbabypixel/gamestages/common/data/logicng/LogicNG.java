package de.dasbabypixel.gamestages.common.data.logicng;

import de.dasbabypixel.gamestages.common.data.attribute.CompilableAttribute;
import de.dasbabypixel.gamestages.common.data.attribute.ImmutableAttribute;
import de.dasbabypixel.gamestages.common.data.attribute.SimpleImmutableAttribute;
import de.dasbabypixel.gamestages.common.data.manager.immutable.AbstractGameStageManager;
import de.dasbabypixel.gamestages.common.data.manager.mutable.SimpleMutableGameStageManager;
import org.jspecify.annotations.NullMarked;
import org.logicng.formulas.FormulaFactory;
import org.logicng.transformations.simplification.AdvancedSimplifier;

@NullMarked
public class LogicNG {
    public static final ImmutableAttribute<AbstractGameStageManager<?>, LogicNG> MANAGER_ATTRIBUTE = new SimpleImmutableAttribute<>();
    public static final CompilableAttribute<SimpleMutableGameStageManager<?, ?>, LogicNG, AbstractGameStageManager<?>> MUTABLE_MANAGER_ATTRIBUTE = MANAGER_ATTRIBUTE.compilable();
    private final FormulaFactory formulaFactory = new FormulaFactory();
    private final AdvancedSimplifier simplifier = new AdvancedSimplifier();

    public FormulaFactory formulaFactory() {
        return formulaFactory;
    }

    public AdvancedSimplifier simplifier() {
        return simplifier;
    }
}
