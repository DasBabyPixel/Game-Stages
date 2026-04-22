package de.dasbabypixel.gamestages.common.data.logicng;

import de.dasbabypixel.gamestages.common.data.attribute.AttributeHolder;
import de.dasbabypixel.gamestages.common.data.attribute.AttributeQuery;
import org.jspecify.annotations.NullMarked;
import org.logicng.formulas.FormulaFactory;
import org.logicng.transformations.simplification.AdvancedSimplifier;

@NullMarked
public class LogicNG {
    public static final AttributeQuery.Holder<AttributeHolder<?>, LogicNG> ATTRIBUTE = AttributeQuery.holder();

    private final FormulaFactory formulaFactory = new FormulaFactory();
    private final AdvancedSimplifier simplifier = new AdvancedSimplifier();

    public FormulaFactory formulaFactory() {
        return formulaFactory;
    }

    public AdvancedSimplifier simplifier() {
        return simplifier;
    }
}
