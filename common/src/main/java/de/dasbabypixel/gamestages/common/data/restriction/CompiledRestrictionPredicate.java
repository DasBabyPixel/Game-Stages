package de.dasbabypixel.gamestages.common.data.restriction;

public sealed interface CompiledRestrictionPredicate permits CachedCompiledRestrictionPredicate {
    boolean test();

    void invalidate();
}
