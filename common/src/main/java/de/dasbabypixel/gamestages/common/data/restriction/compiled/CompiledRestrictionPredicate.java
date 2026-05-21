package de.dasbabypixel.gamestages.common.data.restriction.compiled;

import de.dasbabypixel.gamestages.common.data.restriction.PreparedRestrictionPredicate;
import org.jspecify.annotations.NullMarked;

@NullMarked
public sealed interface CompiledRestrictionPredicate permits CachedCompiledRestrictionPredicate, CompiledRestrictionPredicate.False, CompiledRestrictionPredicate.True {
    True TRUE = new True();
    False FALSE = new False();

    boolean test();

    void invalidate();

    void addNotifier(UpdateNotifier updateNotifier);

    PreparedRestrictionPredicate predicate();

    interface UpdateNotifier {
        void update(boolean newTest);
    }

    final class True implements CompiledRestrictionPredicate {
        @Override
        public boolean test() {
            return true;
        }

        @Override
        public void invalidate() {
        }

        @Override
        public void addNotifier(UpdateNotifier updateNotifier) {
        }

        @Override
        public PreparedRestrictionPredicate predicate() {
            return de.dasbabypixel.gamestages.common.data.restriction.predicates.True.PREPARED;
        }
    }

    final class False implements CompiledRestrictionPredicate {
        @Override
        public boolean test() {
            return false;
        }

        @Override
        public void invalidate() {
        }

        @Override
        public void addNotifier(UpdateNotifier updateNotifier) {
        }

        @Override
        public PreparedRestrictionPredicate predicate() {
            return de.dasbabypixel.gamestages.common.data.restriction.predicates.False.PREPARED;
        }
    }
}
