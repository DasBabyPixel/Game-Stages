package de.dasbabypixel.gamestages.common.data.attribute;

import de.dasbabypixel.gamestages.common.data.compilation.CompilableResource;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CompilableAttributeHolder<Self extends CompilableAttributeHolder<Self, C>, C> extends IAttributeHolder<Self>, CompilableResource<Self, C> {
    <T> T get(CompilableAttribute<? super Self, ?, ?, T> attribute);

    default C compile() {
        return compile(self());
    }
}
